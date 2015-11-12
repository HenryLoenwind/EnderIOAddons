package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.baseclass.TileEnderIOAddons;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.module.CropModule;
import info.loenwind.enderioaddons.machine.afarm.module.CrossBreedModule;
import info.loenwind.enderioaddons.machine.afarm.module.CrossCropModule;
import info.loenwind.enderioaddons.machine.afarm.module.FertilizerModule;
import info.loenwind.enderioaddons.machine.afarm.module.HarvestModule;
import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;
import info.loenwind.enderioaddons.machine.afarm.module.NSEWmodule;
import info.loenwind.enderioaddons.machine.afarm.module.PlantModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteCropsModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteCrossCropsModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteDestroyModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteFertilizerModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteHarvestingModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecutePlantingModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteTillModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteWeedModule;
import info.loenwind.enderioaddons.machine.niard.RadiusIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.farm.FakeFarmPlayer;
import crazypants.enderio.machine.farm.TileFarmStation.ToolType;
import crazypants.enderio.power.BasicCapacitor;

public class TileAfarm extends TileEnderIOAddons {

  public static final int NUM_CONTROL_SLOTS = 6;
  public static final int NUM_CONTROL_STORAGE_SLOTS = 12;
  public static final int NUM_SEED_GHOST_SLOTS = 4;
  public static final int NUM_SEED_STORAGE_SLOTS = 12;
  public static final int NUM_OUTPUT_SLOTS = 18;
  public static final int NUM_TOOL_SLOTS = 2;
  public static final int NUM_FERTILIZER_SLOTS = 3;
  public static final int NUM_CROPSTICK_SLOTS = 4;

  private EntityPlayerMP farmerJoe;

  private static APIv1 agricraft = null;
  private static List<ItemStack> cropSticks;
  private static List<ItemStack> rakes;

  private RadiusIterator itr = null;
  private BlockCoord currentTile = null;

  public TileAfarm() {
    super(new SlotDefinitionAfarm(NUM_CONTROL_SLOTS, NUM_CONTROL_STORAGE_SLOTS, NUM_SEED_GHOST_SLOTS, NUM_SEED_STORAGE_SLOTS, NUM_OUTPUT_SLOTS, NUM_TOOL_SLOTS,
        NUM_FERTILIZER_SLOTS, NUM_CROPSTICK_SLOTS, 1));
    if (agricraft == null) {
      APIBase api = API.getAPI(1);
      if (api.getStatus().isOK() && api.getVersion() == 1) {
        agricraft = (APIv1) api;
        cropSticks = agricraft.getCropsItems();
        rakes = agricraft.getRakeItems();
      }
    }
  }

  @Override
  public void init() {
    super.init();
    currentTask = createTask(null);
  }

  @Override
  public void setWorldObj(World p_145834_1_) {
    super.setWorldObj(p_145834_1_);
    farmerJoe = new FakeFarmPlayer(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId));
  }

  @Override
  public String getMachineName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slotno, @Nullable ItemStack item) {
    if (item == null || item.getItem() == null) {
      return false;
    }
    switch (((SlotDefinitionAfarm) slotDefinition).getSlotType(slotno)) {
    case CONTROL_STORAGE:
      return item.getItem() instanceof IAfarmControlModuleItem;
    case CROPSTICK:
      for (ItemStack cropStick : cropSticks) {
        if (cropStick.getItemDamage() == OreDictionary.WILDCARD_VALUE && cropStick.getItem() == item.getItem()
            && ItemStack.areItemStackTagsEqual(cropStick, item)) {
          return true;
        } else if (ItemUtil.areStacksEqual(cropStick, item)) {
          return true;
        }
      }
      return false;
    case FERTILIZER:
      return agricraft.isSupportedFertilizer(item);
    case SEED:
      return agricraft.isHandledByAgricraft(item);
    case TOOL:
      if (isRake(item)) {
        return getRakeSlot() == -1;
      } else if (ToolType.HOE.itemMatches(item)) {
        return getHoeSlot() == -1;
      } else {
        return false;
      }
    case CONTROL:
      if (item.getItem() instanceof IAfarmControlModuleItem) {
        IAfarmControlModule candidate = ((IAfarmControlModuleItem) item.getItem()).getWorker(item);
        for (IAfarmControlModule installed : getControlModules()) {
          if (!candidate.isCompatibleWith(installed) || !installed.isCompatibleWith(candidate)) {
            return false;
          }
        }
        return true;
      }
      return false;
    case SEED_GHOST:
    case OUTPUT:
    case UPGRADE:
    default:
      return false;
    }
  }

  @Override
  public void setGhostSlotContents(int slot, ItemStack contents) {
    if (((SlotDefinitionAfarm) slotDefinition).getSlotType(slot) == SLOT.SEED_GHOST && (contents == null || agricraft.isHandledByAgricraft(contents))) {
      super.setInventorySlotContents(slot, contents);
      forceClientUpdate = true;
    }
  }

  public int getHoeSlot() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (ToolType.HOE.itemMatches(inventory[i])) {
        return i;
      }
    }
    return -1;
  }

  public int getRakeSlot() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (isRake(inventory[i])) {
        return i;
      }
    }
    return -1;
  }

  private static boolean isRake(ItemStack item) {
    if (item == null || item.getItem() == null) {
      return false;
    }
    for (ItemStack rake : rakes) {
      if ((rake.getItemDamage() == OreDictionary.WILDCARD_VALUE || rake.isItemStackDamageable())
          && (rake.getItem() == item.getItem() && ItemStack.areItemStackTagsEqual(rake, item))) {
        return true;
      } else if (ItemUtil.areStacksEqual(rake, item)) {
        return true;
      }
    }
    return false;
  }

  private int calcPowerUsePerTick() {
    return Math.round(getFarmSize() * 1f + controlModuleCount() * 1f); // TODO cfg
  }

  private int controlModuleCount() {
    int count = 0;
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null) {
        count++;
      }
    }
    return count;
  }

  public int getFarmSize() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return 3; // TODO cfg
    case ACTIVATED_CAPACITOR:
      return 5;
    case ENDER_CAPACITOR:
      return 7;
    }
    return 0;
  }

  public int getDelay() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return 20; // TODO cfg
    case ACTIVATED_CAPACITOR:
      return 10;
    case ENDER_CAPACITOR:
      return 3;
    }
    return Integer.MAX_VALUE;
  }

  @Override
  public void onCapacitorTypeChange() {
    int ppt = calcPowerUsePerTick();
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 250000, ppt));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 500000, ppt));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 1000000, ppt));
      break;
    }
    currentTask = createTask(null);
    itr = null;
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return createTask(null);
  }

  @Override
  protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (canTick(redstoneChecksPassed) && redstoneChecksPassed) {
      return doTick();
    }
    return false;
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if (redstoneChecksPassed) {
      if (usePower(getPowerUsePerTick()) == 0) {
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  protected boolean doTick() {
    if (agricraft != null && farmerJoe != null && shouldDoWorkThisTick(getDelay())) {
      if (itr == null) {
        itr = new RadiusIterator(getLocation(), getFarmSize());
      }
      while (currentTile == null || currentTile.equals(getLocation())) {
        currentTile = itr.next();
      }
      WorkTile tile = new WorkTile(currentTile, this, agricraft, farmerJoe);
      List<IAfarmControlModule> controls = getControlModules();
      for (IAfarmControlModule control : controls) {
        control.doWork(tile);
        // TODO add config value for this
        //   Log.info(tile + " after " + control);
      }
      if (!tile.doneSomething) {
        currentTile = itr.next();
      }
    }
    return false;
  }

  private static final IAfarmControlModuleComparator moduleComperator = new IAfarmControlModuleComparator();
  private static final List<IAfarmControlModule> standardModules = new ArrayList<>();
  static {
    standardModules.add(new NSEWmodule());
    standardModules.add(new HarvestModule());
    standardModules.add(new PlantModule());
    standardModules.add(new CrossCropModule());
    standardModules.add(new CropModule());
    standardModules.add(new FertilizerModule());
    standardModules.add(new ExecuteHarvestingModule());
    standardModules.add(new ExecuteDestroyModule());
    standardModules.add(new ExecutePlantingModule());
    standardModules.add(new ExecuteCropsModule());
    standardModules.add(new ExecuteCrossCropsModule());
    standardModules.add(new ExecuteWeedModule());
    standardModules.add(new ExecuteTillModule());
    standardModules.add(new ExecuteFertilizerModule());
    Collections.sort(standardModules, moduleComperator);
  }

  private List<IAfarmControlModule> getControlModules() {
    List<IAfarmControlModule> result = new ArrayList<>();
    result.addAll(standardModules);
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null && inventory[i].getItem() instanceof IAfarmControlModuleItem) {
        result.add(((IAfarmControlModuleItem) inventory[i].getItem()).getWorker(inventory[i]));
      }
    }
    Collections.sort(result, moduleComperator);
    return result;
  }

  public boolean twoGhosts() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null && inventory[i].getItem() instanceof IAfarmControlModuleItem
          && ((IAfarmControlModuleItem) inventory[i].getItem()).getWorker(inventory[i]) instanceof CrossBreedModule) {
        return true;
      }
    }
    return false;
  }

  private static class IAfarmControlModuleComparator implements Comparator<IAfarmControlModule> {

    @Override
    public int compare(IAfarmControlModule o1, IAfarmControlModule o2) {
      return Integer.compare(o1.getPriority(), o2.getPriority());
    }

  }

  @Override
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    if (faceHit != ForgeDirection.DOWN) {
      return mode == IoMode.DISABLED;
    }
    return super.supportsMode(faceHit, mode);
  }

  @Override
  public boolean isSideDisabled(int var1) {
    if (var1 != ForgeDirection.DOWN.ordinal()) {
      return true;
    }
    return super.isSideDisabled(var1);
  }

  @Override
  public IoMode getIoMode(ForgeDirection face) {
    if (face != ForgeDirection.DOWN) {
      return IoMode.DISABLED;
    }
    return super.getIoMode(face);
  }

  @Override
  public void setIoMode(ForgeDirection faceHit, IoMode mode) {
    if (faceHit != ForgeDirection.DOWN) {
      super.setIoMode(faceHit, IoMode.DISABLED);
      return;
    }
    super.setIoMode(faceHit, mode);
  }

}
