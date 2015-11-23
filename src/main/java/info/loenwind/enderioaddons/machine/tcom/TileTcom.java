package info.loenwind.enderioaddons.machine.tcom;

import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import static info.loenwind.enderioaddons.EnderIOAddons.mode24;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.SideRestriction;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.tcom.engine.EngineTcom;
import info.loenwind.enderioaddons.machine.tcom.engine.Mats;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.Fluid;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.util.ItemUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileTcom extends AbstractTileFramework implements IFrameworkMachine, IProgressTile {

  @Store
  protected EngineTcom engine = new EngineTcom(Config.tcomRecyclingLossFix.getFloat(), Config.tcomRecyclingLossChanced.getFloat());

  protected @Store({ SAVE, ITEM }) int progress_in = 0; // in ticks
  protected @Store({ SAVE, ITEM }) ItemStack buffer_in = null;
  protected @Store({ SAVE, ITEM }) int progress_enchant = 0; // in ticks
  protected @Store({ SAVE, ITEM }) ItemStack buffer_enchant_result = null;
  protected @Store({ SAVE, ITEM }) ItemStack buffer_enchant_original = null;
  protected EntityPlayerMP buffer_enchant_player = null;

  public TileTcom() {
    super(new SlotDefinition(2, 2, 0));
  }

  @Override
  public String getMachineName() {
    return BlockTcom.ModObject_blockTcom.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      if (i == slotDefinition.getMinInputSlot()) {
        return engine.addable(itemstack);
      } else {
        return engine.isValidTarget(itemstack);
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    if (rsCheckPassed) {
      if (!process_in_progress() && progress_in == 0 && inventory[slotDefinition.getMinInputSlot()] != null) {
        takeCareOfStrayItem(buffer_in);
        buffer_in = inventory[slotDefinition.getMinInputSlot()].copy();
        if (buffer_in.stackSize == 1) {
          inventory[slotDefinition.getMinInputSlot()] = null;
        } else {
          inventory[slotDefinition.getMinInputSlot()].stackSize--;
          buffer_in.stackSize = 1;
        }
        progress_in = 1;
        markDirty();
        playSound_in();
        updateClients();
      }
      process_enchant_progress();
    }
    return false;
  }

  /**
   * Makes sure the given item is moved somewhere. It'll prefer an output slot
   * if one is free, but will drop the item to the ground if not. It won't try
   * such things as stacking the item because it is just a way to get rid of
   * something that shouldn't have existed in the first place. Make sure to
   * remove the item from wherever it was afterwards.
   * 
   * Save to be called with null.
   */
  private void takeCareOfStrayItem(@Nullable ItemStack item) {
    if (item != null) {
      if (inventory[slotDefinition.getMinOutputSlot()] == null) {
        inventory[slotDefinition.getMinOutputSlot()] = item;
        markDirty();
      } else if (inventory[slotDefinition.getMinOutputSlot() + 1] == null) {
        inventory[slotDefinition.getMinOutputSlot() + 1] = item;
        markDirty();
      } else {
        ItemUtil.spawnItemInWorldWithRandomMotion(getWorldObj(), buffer_in, xCoord, yCoord, zCoord);
      }
    }
  }

  private boolean process_in_progress() {
    if (progress_in > 0 && usePower(Config.tcomRecyclingPowerPerTick.getInt())) {
      progress_in++;
      if (progress_in > Config.tcomRecyclingTicksPerItem.getInt()) {
        if (buffer_in != null && engine.add(buffer_in)) {
          updateClients();
        } else {
          takeCareOfStrayItem(buffer_in);
        }
        markDirty();
        buffer_in = null;
        progress_in = 0;
      }
      return true;
    }
    return false;
  }

  public void extractItems(Mats mat, EntityPlayerMP player) {
    ItemStack target;
    if (!engine.canGet(mat)) {
      playSound_fail();
      return;
    }
    if (inventory[slotDefinition.getMinOutputSlot()] == null) {
      target = mat.getItemStack().copy();
      target.stackSize = 0;
    } else if (mat.isSame(inventory[slotDefinition.getMinOutputSlot()])) {
      target = inventory[slotDefinition.getMinOutputSlot()];
      if (target.stackSize >= target.getMaxStackSize()) {
        playSound_fail();
        return;
      }
    } else {
      playSound_fail();
      return;
    }
    while (target.stackSize < target.getMaxStackSize()) {
      if (canUsePower(Config.tcomExtractingPowerPerItem.getInt())) {
        if (engine.get(mat)) {
          target.stackSize++;
          usePower(Config.tcomExtractingPowerPerItem.getInt());
        } else {
          break;
        }
      } else {
        playSound_fail();
        break;
      }
    }
    inventory[slotDefinition.getMinOutputSlot()] = target.stackSize > 0 ? target : null;
    markDirty();
    updateClient(player);
  }

  public void extractEnchantment(int id, EntityPlayerMP player) {
    if (progress_enchant > 0 || inventory[slotDefinition.getMinOutputSlot() + 1] != null || inventory[slotDefinition.getMinInputSlot() + 1] == null) {
      playSound_fail();
      return;
    }
    if (engine.canAddEnchantment(inventory[slotDefinition.getMinInputSlot() + 1], id)) {
      buffer_enchant_original = inventory[slotDefinition.getMinInputSlot() + 1].copy();
      buffer_enchant_original.stackSize = 1;
      buffer_enchant_result = buffer_enchant_original.copy();
      if (engine.addEnchantment(buffer_enchant_result, id,
          (int) getEnchantPower() + buffer_enchant_result.getItem().getItemEnchantability(buffer_enchant_result) / 2)) {
        if (inventory[slotDefinition.getMinInputSlot() + 1].stackSize == 1) {
          inventory[slotDefinition.getMinInputSlot() + 1] = null;
        } else {
          inventory[slotDefinition.getMinInputSlot() + 1].stackSize--;
        }
        progress_enchant = 1;
        buffer_enchant_player = player;
        markDirty();
        updateClient(player);
      } else {
        buffer_enchant_result = buffer_enchant_original = null;
        playSound_fail();
      }
    } else {
      playSound_fail();
    }
  }

  private void process_enchant_progress() {
    if (progress_enchant > 0 && usePower(Config.tcomEnchantingPowerPerTick.getInt())) {
      progress_enchant++;
      if (progress_enchant > Config.tcomEnchantingTicksPerItem.getInt()) {
        if (buffer_enchant_result != null && buffer_enchant_original != null) {
          int levels = countEnchantmentLevels(buffer_enchant_result);
          if (levels > getWorldObj().rand.nextInt(levels + 10 + (int) getEnchantPower()
              + buffer_enchant_result.getItem().getItemEnchantability(buffer_enchant_result))) {
            buffer_enchant_result = buffer_enchant_original;
            usePower(Config.tcomEnchantingPowerPerFailure.getInt());
            playSound_enchant_fail();
          } else {
            playSound_enchant();
          }
          if (inventory[slotDefinition.getMinOutputSlot() + 1] == null) {
            inventory[slotDefinition.getMinOutputSlot() + 1] = buffer_enchant_result;
          } else {
            takeCareOfStrayItem(buffer_enchant_result);
          }
          updateClient(buffer_enchant_player);
        } else {
          takeCareOfStrayItem(buffer_enchant_original);
        }
        markDirty();
        buffer_enchant_result = buffer_enchant_original = null;
        buffer_enchant_player = null;
        progress_enchant = 0;
      }
    }
  }

  private static int countEnchantmentLevels(ItemStack itemStack) {
    int result = 0;
    Map<Integer, Integer> enchantmentsOnItemStack = EnchantmentHelper.getEnchantments(itemStack);
    for (Integer level : enchantmentsOnItemStack.values()) {
      result += level;
    }
    return result;
  }

  private float getEnchantPower() {
    float power = 0;

    for (int j = -1; j <= 1; ++j) {
      for (int k = -1; k <= 1; ++k) {
        if ((j != 0 || k != 0) && getWorldObj().isAirBlock(xCoord + k, yCoord, zCoord + j) && getWorldObj().isAirBlock(xCoord + k, yCoord + 1, zCoord + j)) {
          power += ForgeHooks.getEnchantPower(getWorldObj(), xCoord + k * 2, yCoord, zCoord + j * 2);
          power += ForgeHooks.getEnchantPower(getWorldObj(), xCoord + k * 2, yCoord + 1, zCoord + j * 2);

          if (k != 0 && j != 0) {
            power += ForgeHooks.getEnchantPower(getWorldObj(), xCoord + k * 2, yCoord, zCoord + j);
            power += ForgeHooks.getEnchantPower(getWorldObj(), xCoord + k * 2, yCoord + 1, zCoord + j);
            power += ForgeHooks.getEnchantPower(getWorldObj(), xCoord + k, yCoord, zCoord + j * 2);
            power += ForgeHooks.getEnchantPower(getWorldObj(), xCoord + k, yCoord + 1, zCoord + j * 2);
          }
        }
      }
    }

    return power;
  }

  public void updateClient(EntityPlayerMP player) {
    try {
      if (player != null && player.openContainer instanceof ContainerTcom) {
        PacketHandler.sendTo(new PacketTcomUpdate(this, true), player);
      }
    } catch (Throwable t) {
    }
  }

  public void updateClients() {
    PacketHandler.sendToAllAround(new PacketTcomUpdate(this, false), this);
  }

  private static final String[] soundsIds = { "machine.tcom.in", "machine.tcom.enchant", "machine.tcom.fail", "machine.tcom.enchant.fail" };
  private static final int[] soundDurations = { 45, 36, 5, 41 }; // 2.226s, 1.777s, 0.239s, 2.039s
  private final long[] lastSoundTick = new long[soundsIds.length];

  private void playSound(int id) {
    if (crazypants.enderio.config.Config.machineSoundsEnabled && lastSoundTick[id] <= EnderIO.proxy.getTickCount()) {
      lastSoundTick[id] = EnderIO.proxy.getTickCount() + soundDurations[id];
      getWorldObj().playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, EnderIOAddons.DOMAIN + ":" + soundsIds[id], getVolume(), 1.0f);
    }
  }

  private void playSound_in() {
    playSound(0);
  }

  private void playSound_enchant() {
    playSound(1);
  }

  private void playSound_fail() {
    playSound(2);
  }

  private void playSound_enchant_fail() {
    playSound(3);
  }

  public boolean canUsePower(Float wantToUse) {
    int w = wantToUse.intValue();
    return mode24 || !((w < 1 ? 1 : w) > getEnergyStored());
  }

  public boolean canUsePower(int wantToUse) {
    return mode24 || !(wantToUse > getEnergyStored());
  }

  public boolean usePower(Float wantToUse) {
    int w = wantToUse.intValue();
    return mode24 || usePower(w < 1 ? 1 : w);
  }

  public boolean usePower(int wantToUse) {
    if (mode24) {
      return true;
    } else if (wantToUse > getEnergyStored()) {
      return false;
    } else {
      setEnergyStored(getEnergyStored() - wantToUse);
      return true;
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    setCapacitor(new BasicCapacitor(Config.tcomPowerIntakePerTick.getInt(), 10000, Config.tcomEnchantingPowerPerTick.getInt()
        + Config.tcomRecyclingPowerPerTick.getInt()));
  }

  @Override
  public boolean hasTank(@Nonnull TankSlot tankSlot) {
    return tankSlot == TankSlot.FRONT_RIGHT;
  }

  @Override
  public Fluid getTankFluid(@Nonnull TankSlot tankSlot) {
    return null;
  }

  @Override
  public boolean hasController() {
    return true;
  }

  @Override
  public String getControllerModelName() {
    return BlockTcom.blockTcom.getControllerModelName();
  }

  @Override
  public boolean renderSlot(@Nonnull TankSlot tankSlot) {
    return false; // is handled by our renderer
  }

  @Override
  public IIcon getSlotIcon(@Nonnull TankSlot tankSlot, int side) {
    return null;
  }

  @Override
  public float getProgress() {
    return progress_in * 1000 + progress_enchant;
  }

  @SideOnly(Side.CLIENT)
  public float getWorkProgress_in() {
    return progress_in / Config.tcomRecyclingTicksPerItem.getFloat();
  }

  @SideOnly(Side.CLIENT)
  public float getWorkProgress_enchant() {
    return progress_enchant / Config.tcomEnchantingTicksPerItem.getFloat();
  }

  @Override
  public void setProgress(float progress) {
    if (progress > 0) {
      int p = (int) progress;
      progress_in = p / 1000;
      progress_enchant = p - progress_in * 1000;
    } else {
      progress_in = 0;
      progress_enchant = 0;
    }
  }

  @Override
  public TileEntity getTileEntity() {
    return this;
  }

  @SideRestriction(Side.CLIENT)
  public final int[] renderData = { 160, 1, 15, 0 };
  @SideRestriction(Side.CLIENT)
  public long lastRenderTick = 0;

}
