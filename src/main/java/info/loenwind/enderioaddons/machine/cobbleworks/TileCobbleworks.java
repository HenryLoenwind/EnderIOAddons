package info.loenwind.enderioaddons.machine.cobbleworks;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.enderioaddons.EnderIOAddons.mode24;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ItemStackHolder;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileCobbleworks extends AbstractTileFramework implements IFrameworkMachine {

  static final int SLOTS_PER_WORK = 4;
  static final int WORKS = 3;
  @Nonnull
  private final Mapping[] outputMapping = new Mapping[1 + SLOTS_PER_WORK * WORKS];

  public static final @ItemStackHolder("EnderIO:blockCrafter") ItemStack blockCrafter = null;
  public static final @ItemStackHolder("EnderIO:blockAlloySmelter") ItemStack blockAlloySmelter = null;
  public static final @ItemStackHolder("EnderIO:blockSagMill") ItemStack blockSagMill = null;

  private boolean inputsChanged = true;
  private int capTickLimit = 0;

  // will be sent to the client to allow for visual effects
  @Store({ CLIENT })
  private int active = 0;
  @Store({ CLIENT })
  private int activeWorks = 0;

  public TileCobbleworks() {
    super(new SlotDefinition(WORKS, 1 + SLOTS_PER_WORK * WORKS, 1));
  }

  @Override
  public String getMachineName() {
    return BlockCobbleworks.ModObject_blockCobbleworks.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
    inputsChanged = true;
    computeOutputMapping();
  }

  private static boolean isUpgradeMachine(@Nullable ItemStack stack) {
    if (stack == null) {
      return false;
    }
    Item item = stack.getItem();
    return item == blockCrafter.getItem() || item == blockAlloySmelter.getItem() || item == blockSagMill.getItem();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      return isUpgradeMachine(itemstack);
    } else if (slotDefinition.isOutputSlot(i)) {
      Mapping mapping = outputMapping[i - slotDefinition.minOutputSlot];
      return mapping != null && itemstack != null && mapping.itemStack.isItemEqual(itemstack);
    } else {
      return false;
    }
  }

  @Override
  public int getInventoryStackLimit(int slot) {
    if (slotDefinition.isInputSlot(slot)) {
      return 1;
    }
    return 64;
  }

  @Override
  public int getInventoryStackLimit() {
    return getInventoryStackLimit(0);
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed && active > 0;
  }

  boolean isActive(int work) {
    if (work == 0) {
      return activeWorks != 0;
    } else if (work == 1) {
      return (activeWorks & 0b1111111111110) != 0;
    } else if (work == 2) {
      return (activeWorks & 0b1111111100000) != 0;
    } else if (work == 3) {
      return (activeWorks & 0b1111000000000) != 0;
    } else {
      return false;
    }
  }

  @Override
  protected void updateEntityClient() {
    if (active > 0) {
      active--;
    }
    super.updateEntityClient();
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    boolean updateClient = computeOutputMapping();
    if (rsCheckPassed && reCraft()) {
      active = 40;
      return true;
    } else {
      if (active > 0) {
        active--;
        return updateClient;
      } else if (activeWorks != 0) {
        activeWorks = 0;
        return true;
      } else {
        return updateClient;
      }
    }
  }

  private int outputSlotNo(int no) {
    return no + slotDefinition.minOutputSlot;
  }

  @Nullable
  private ItemStack outputSlot(int no) {
    return inventory[outputSlotNo(no)];
  }

  private void outputSlot(int no, @Nullable ItemStack is) {
    inventory[outputSlotNo(no)] = is;
  }

  int outputSlotNo(int work, int no) {
    return outputSlotNo(outputMappingNo(work, no));
  }

  private static int outputMappingNo(int work, int no) {
    return work == 0 ? 0 : (work - 1) * SLOTS_PER_WORK + no;
  }

  private void outputMapping(int work, int no, @Nullable Mapping mapping) {
    outputMapping[outputMappingNo(work, no)] = mapping;
  }

  @Nullable
  private Mapping outputMapping(int work, int no) {
    return outputMapping[outputMappingNo(work, no)];
  }

  int inputSlotNo(int no) {
    return no + slotDefinition.minInputSlot - 1;
  }

  @Nullable
  private ItemStack inputSlot(int no) {
    return inventory[inputSlotNo(no)];
  }

  private static void clear(@Nonnull Object[] list) {
    for (int i = 0; i < list.length; i++) {
      list[i] = null;
    }
  }

  @Nonnull
  private List<Mapping> getInputForWork(int work) {
    List<Mapping> result = new ArrayList<Mapping>();
    if (work == 1) {
      result.add(outputMapping(0, 0));
    } else {
      for (int i = 1; i <= SLOTS_PER_WORK; i++) {
        result.add(outputMapping(work - 1, i));
      }
    }

    return result;
  }

  private boolean computeOutputMapping() {
    if (!inputsChanged || worldObj == null || worldObj.isRemote) {
      return false;
    }
    inputsChanged = false;

    clear(outputMapping);

    outputMapping(0, 0,
        new Mapping(OperationType.NONE, new ItemStack(Item.getItemFromBlock(Blocks.cobblestone), 1), Config.cobbleWorksRfPerCobblestone.getInt(), -1, null, 0));

    for (int work = 1; work <= WORKS; work++) {
      ItemStack machine = inputSlot(work);
      List<Mapping> input = getInputForWork(work);
      List<Mapping> output = new ArrayList<Mapping>();
      if (machine != null && machine.getItem() != null && !input.isEmpty()) {
        for (Mapping mapping : input) {
          if (mapping != null) {
            if (machine.getItem() == blockCrafter.getItem()) {
              computeCrafterOutput(output, mapping);
            } else if (machine.getItem() == blockAlloySmelter.getItem()) {
              computeMachineOutput(output, mapping, ModObject.blockAlloySmelter.unlocalisedName, OperationType.SMELTING);
            } else if (machine.getItem() == blockSagMill.getItem()) {
              computeMachineOutput(output, mapping, ModObject.blockSagMill.unlocalisedName, OperationType.CRUSHING);
            } else {
              // invalid machine...
            }
          }
        }
      }
      
      for (Mapping mapping : output) {
        boolean done = false;
        // do we already have this item in a previous step?
        Mapping om0 = outputMapping(0, 0);
        if (om0 != null && om0.itemStack.isItemEqual(mapping.itemStack)) {
          done = true;
        }
        for (int w = 1; !done && w < work; w++) {
          for (int i = 1; !done && i <= SLOTS_PER_WORK; i++) {
            Mapping om = outputMapping(w, i);
            if (om != null && om.itemStack.isItemEqual(mapping.itemStack)) {
              done = true;
            }
          }
        }

        // do we already have it in this step? If yes, keep the cheaper recipe
        for (int i = 1; !done && i <= SLOTS_PER_WORK; i++) {
          Mapping om = outputMapping(work, i);
          if (om != null && om.itemStack.isItemEqual(mapping.itemStack)) {
            done = true;
            if (om.costInRF > mapping.costInRF) {
              mapping.position = outputMappingNo(work, i);
              outputMapping(work, i, mapping);
            }
          }
        }

        // can we add it to this work's slots?
        for (int i = 1; !done && i <= SLOTS_PER_WORK; i++) {
          if (outputMapping(work, i) == null) {
            done = true;
            mapping.position = outputMappingNo(work, i);
            outputMapping(work, i, mapping);
          }
        }
        if (!done) {
          // More outputs than slots. Log this?
        }
      }
    }
    return true;
  }

  private static void computeMachineOutput(@Nonnull List<Mapping> output, @Nonnull Mapping input, String machineName,
      @Nonnull OperationType operationType) {
    ItemStack stackcopy = input.itemStack.copy();
    stackcopy.stackSize = 1; // otherwise we'd get a 200% energy bonus on
                             // vanilla smelting
    MachineRecipeInput mri = new MachineRecipeInput(0, stackcopy);
    List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(machineName, mri);
    for (IMachineRecipe recipe : recipes) {
      ResultStack[] completedResult = recipe.getCompletedResult(0, mri);
      for (ResultStack resultStack : completedResult) {
        if (resultStack.item != null) {
          output.add(new Mapping(operationType, resultStack.item.copy(), recipe.getEnergyRequired(mri), input.position, input.itemStack, 1));
        }
      }
    }
  }

  private void computeCrafterOutput(@Nonnull List<Mapping> output, @Nonnull Mapping input) {
    InventoryCrafting inv = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer var1) {
        return false;
      }
    }, 3, 3);
    // 3x3
    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, input.itemStack);
    }
    ItemStack crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), crazypants.enderio.config.Config.crafterRfPerCraft, input.position, input.itemStack, 9));
    }
    // 2x2
    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, null);
    }
    inv.setInventorySlotContents(0, input.itemStack);
    inv.setInventorySlotContents(1, input.itemStack);
    inv.setInventorySlotContents(3, input.itemStack);
    inv.setInventorySlotContents(4, input.itemStack);
    crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), crazypants.enderio.config.Config.crafterRfPerCraft, input.position, input.itemStack, 4));
    }
    // 1x1
    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, null);
    }
    inv.setInventorySlotContents(0, input.itemStack);
    crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), crazypants.enderio.config.Config.crafterRfPerCraft, input.position, input.itemStack, 1));
    }
    // 3x2 (glass panes, cobblestone walls)
    for (int i = 0; i < 6; i++) {
      inv.setInventorySlotContents(i, input.itemStack);
    }
    for (int i = 6; i < 9; i++) {
      inv.setInventorySlotContents(i, null);
    }
    crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), crazypants.enderio.config.Config.crafterRfPerCraft, input.position, input.itemStack, 4));
    }
  }

  private int applyDiscounts(int RFcost, @Nonnull OperationType operationType) {
    int cost = RFcost;
    switch (operationType) {
    case CRAFTING:
      cost -= cost * Config.cobbleWorksRfDiscountForCrafting.getInt() / 100;
      break;
    case CRUSHING:
      cost -= cost * Config.cobbleWorksRfDiscountForCrushing.getInt() / 100;
      break;
    case SMELTING:
      cost -= cost * Config.cobbleWorksRfDiscountForSmelting.getInt() / 100;
      break;
    default:
      break;
    }
    switch (getCapacitorType()) {
    case ENDER_CAPACITOR:
      cost -= cost * Config.cobbleWorksRfDiscountPerUpgrade.getInt() / 100;
    case ACTIVATED_CAPACITOR:
      cost -= cost * Config.cobbleWorksRfDiscountPerUpgrade.getInt() / 100;
    default:
    }
    if (cost < 0) {
      cost = 0;
    }
    return cost;
  }

  private boolean reCraft() {
    if (capTickLimit < getCapacitor().getMaxEnergyExtracted()) {
      capTickLimit += getCapacitor().getMaxEnergyExtracted();
    }
    boolean reSync = false;
    activeWorks = 0;
    for (int i = outputMapping.length - 1; i >= 0; i--) {
      Mapping mapping = outputMapping[i];
      if (mapping != null) {
        ItemStack slot = outputSlot(mapping.position);
        if (slot == null) {
          slot = mapping.itemStack.copy();
          slot.stackSize = 0;
        }
        if (mapping.itemStack.isItemEqual(slot)) {
          boolean goodToGo = true;
          ItemStack parentStack = null;
          if (mapping.parent >= 0) {
            parentStack = outputSlot(mapping.parent);
            if (parentStack == null || parentStack.stackSize < mapping.inputAmount || !parentStack.isItemEqual(mapping.inputItemStack)) {
              goodToGo = false;
            }
          }
          boolean parentChanged = false;
          boolean slotChanged = false;
          while (goodToGo && slot.stackSize < slot.getMaxStackSize()
              && slot.stackSize + mapping.itemStack.stackSize <= slot.getMaxStackSize()) {
            if (usePower(applyDiscounts(mapping.costInRF, mapping.operationType))) {
              slot.stackSize += mapping.itemStack.stackSize;
              slotChanged = true;
              activeWorks |= 1 << i;
              if (parentStack != null) {
                parentStack.stackSize -= mapping.inputAmount;
                parentChanged = true;
                if (parentStack.stackSize < mapping.inputAmount) {
                  goodToGo = false;
                  if (parentStack.stackSize <= 0) {
                    parentStack = null;
                  }
                }
              }
            } else {
              goodToGo = false;
            }
          }
          if (parentChanged) {
            outputSlot(mapping.parent, parentStack);
            reSync = true;
          }
          if (slotChanged) {
            if (slot.stackSize > 0) {
              outputSlot(mapping.position, slot);
            } else {
              outputSlot(mapping.position, null);
            }
            reSync = true;
          }
        }
      } else {
        // there is a wrong item in an output slot. Do nothing.
      }
    }
    return reSync;
  }

  public boolean usePower(int wantToUse) {
    if (mode24) {
      return true;
    } else if (wantToUse > getEnergyStored() || (wantToUse > capTickLimit && capTickLimit < getCapacitor().getMaxEnergyExtracted())) {
      return false;
    } else {
      setEnergyStored(getEnergyStored() - wantToUse);
      capTickLimit -= wantToUse;
      return true;
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(crazypants.enderio.config.Config.powerConduitTierOneRF, 100000, crazypants.enderio.config.Config.powerConduitTierOneRF));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(crazypants.enderio.config.Config.powerConduitTierTwoRF, 200000, crazypants.enderio.config.Config.powerConduitTierTwoRF));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(crazypants.enderio.config.Config.powerConduitTierThreeRF, 500000,
          crazypants.enderio.config.Config.powerConduitTierThreeRF));
      break;
    }
    inputsChanged = true;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    if (slotDefinition.isInputSlot(slot) && differs(contents, inventory[slot])) {
      inputsChanged = true;
    }
    super.setInventorySlotContents(slot, contents);
  }

  private static boolean differs(ItemStack a, ItemStack b) {
    if (a == null && b == null) {
      return false;
    }
    if (a == null || b == null) {
      return true;
    }
    return a.getItem() != b.getItem();
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if (slotDefinition.isInputSlot(fromSlot)) {
      inputsChanged = true;
    }
    return super.decrStackSize(fromSlot, amount);
  }

  private static enum OperationType {
    NONE, CRAFTING, SMELTING, CRUSHING;
  }

  private static class Mapping {
    @Nonnull
    final OperationType operationType;
    @Nonnull
    final ItemStack itemStack;
    final int costInRF;
    final int parent;
    int position = 0;
    final int inputAmount;
    @Nullable
    final ItemStack inputItemStack;

    public Mapping(@Nonnull OperationType operationType, ItemStack itemStack, int costInRF, int parent, @Nullable ItemStack inputItemStack, int inputAmount) {
      if (itemStack == null) {
        throw new NullPointerException("Unexpected NULL ItemStack");
      }
      this.operationType = operationType;
      this.itemStack = itemStack;
      this.costInRF = costInRF;
      this.parent = parent;
      this.inputItemStack = inputItemStack;
      this.inputAmount = inputAmount;
    }
  }

  @Override
  public boolean hasTank(@Nonnull TankSlot tankSlot) {
    return true;
  }

  @Override
  public Fluid getTankFluid(@Nonnull TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
    case BACK_RIGHT:
      return FluidRegistry.LAVA;
    case FRONT_RIGHT:
    case BACK_LEFT:
      return FluidRegistry.WATER;
    }
    return null;
  }

  @Override
  public boolean hasController() {
    return true;
  }

  @Override
  public String getControllerModelName() {
    return BlockCobbleworks.blockCobbleworks.getControllerModelName();
  }

  @Override
  public boolean renderSlot(@Nonnull TankSlot tankSlot) {
    if (tankSlot != TankSlot.FRONT_LEFT) {
      ItemStack stack = inputSlot(tankSlot.ordinal());
      return stack != null && stack.getItem() != null;
    } else {
      return false;
    }
  }

  @Override
  public IIcon getSlotIcon(@Nonnull TankSlot tankSlot, int side) {
    ItemStack stack = inputSlot(tankSlot.ordinal());
    if (stack == null) {
      return null;
    } else if (stack.getItem() == blockCrafter.getItem()) {
      return EnderIO.blockCrafter.getIcon(side, 0);
    } else if (stack.getItem() == blockAlloySmelter.getItem()) {
      return EnderIO.blockAlloySmelter.getIcon(side, 0);
    } else if (stack.getItem() == blockSagMill.getItem()) {
      return EnderIO.blockCrusher.getIcon(side, 0);
    }
    return null;
  }

  @Override
  public String getSoundName() {
    return "machine.cobbleworks";
  }

}
