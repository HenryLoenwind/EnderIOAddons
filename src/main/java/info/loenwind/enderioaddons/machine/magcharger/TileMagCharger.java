package info.loenwind.enderioaddons.machine.magcharger;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.enderioaddons.EnderIOAddons.mode24;
import static info.loenwind.enderioaddons.config.Config.magcMagnetizingPowerPerTick;
import static info.loenwind.enderioaddons.config.Config.magcMagnetizingTicksPerItem;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.Profiler;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.flag.ItemFlag;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.network.INetworkUpdatable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileMagCharger extends AbstractTileFramework implements INetworkUpdatable {

  protected @Store int progress = -1; // in ticks, counting down
  public @Store({ CLIENT }) int itemsInQueue = 0;

  @SideOnly(Side.CLIENT)
  public int renderOffset = 0;
  @SideOnly(Side.CLIENT)
  public float renderLastProgress = 0;

  public TileMagCharger() {
    super(new SlotDefinition(1, 1, 1));
  }

  @Override
  public String getMachineName() {
    return BlockMagCharger.ModObject_blockMagCharger.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void setWorldObj(World p_145834_1_) {
    super.setWorldObj(p_145834_1_);
    EnderIOAddons.network.register(this);
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      return itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemFlag && itemstack.getItemDamage() != 0;
    } else {
      return false;
    }
  }

  @Override
  public boolean isActive() {
    return redstoneCheckPassed && progress >= 0;
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    boolean needClientUpdate = false;
    if (rsCheckPassed) {
      long id = Profiler.server.start();
      if (progress > 0 && usePower(magcMagnetizingPowerPerTick.getInt())) {
        progress -= getCapacitorBasedSpeed();
        if (progress < 0) {
          progress = 0;
        }
      }
      if (progress == 0 && (outputSlotIsEmpty() || outputSlotsIsNotFull())) {
        if (outputSlotIsEmpty()) {
          inventory[slotDefinition.getMinOutputSlot()] = new ItemStack(BlockFlag.blockFlag, 1, 0);
        } else {
          inventory[slotDefinition.getMinOutputSlot()].stackSize++;
        }
        markDirty();
        usePower(magcMagnetizingPowerPerTick.getInt());
        progress = -1;
        needClientUpdate = true;
      }
      if (progress < 0 && inputSlotIsNotEmpty() && (outputSlotIsEmpty() || outputSlotsIsNotFull())) {
        progress = magcMagnetizingTicksPerItem.getInt() + 1;
        inventory[slotDefinition.getMinInputSlot()].stackSize--;
        if (inventory[slotDefinition.getMinInputSlot()].stackSize <= 0) {
          inventory[slotDefinition.getMinInputSlot()] = null;
        }
        markDirty();
        playSound_in();
        needClientUpdate = true;
      }
      Profiler.server.stop(id, "magcharger tick");
    }
    itemsInQueue = inputSlotIsNotEmpty() ? inventory[slotDefinition.getMinInputSlot()].stackSize : 0;
    return needClientUpdate;
  }

  private boolean inputSlotIsNotEmpty() {
    return inventory[slotDefinition.getMinInputSlot()] != null;
  }

  private boolean outputSlotsIsNotFull() {
    return inventory[slotDefinition.getMinOutputSlot()].stackSize < inventory[slotDefinition.getMinOutputSlot()].getMaxStackSize();
  }

  private boolean outputSlotIsEmpty() {
    return inventory[slotDefinition.getMinOutputSlot()] == null;
  }

  private static final String[] soundsIds = { "machine.magcharger", "machine.magcharger.cont" };
  private static final int[] soundDurations = { 19, 10 };
  private final long[] lastSoundTick = new long[soundsIds.length];

  public void playSound(int id) {
    if (crazypants.enderio.config.Config.machineSoundsEnabled && lastSoundTick[id] <= EnderIO.proxy.getTickCount()) {
      lastSoundTick[id] = EnderIO.proxy.getTickCount() + soundDurations[id];
      getWorldObj().playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, EnderIOAddons.DOMAIN + ":" + soundsIds[id], getVolume(), 1.0f);
    }
  }

  private void playSound_in() {
    playSound(0);
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
    return usePower(w < 1 ? 1 : w);
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
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.magcPowerIntakePerTick.getInt(), 10000, Config.magcMagnetizingPowerPerTick.getInt()));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.magcPowerIntakePerTick.getInt() * 2, 10000, Config.magcMagnetizingPowerPerTick.getInt()));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.magcPowerIntakePerTick.getInt() * 8, 10000, Config.magcMagnetizingPowerPerTick.getInt()));
      break;
    }
  }

  private int getCapacitorBasedSpeed() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return 1;
    case ACTIVATED_CAPACITOR:
      return 2;
    case ENDER_CAPACITOR:
      return 8;
    }
    return 1;
  }

  public float getProgressScaled() {
    if (progress <= 0) {
      return 0;
    } else {
      return ((magcMagnetizingTicksPerItem.getInt() + 1) - progress) / (magcMagnetizingTicksPerItem.getFloat() + 1f);
    }
  }

  @Override
  public void networkUpdate(int id, int data) {
    switch (id) {
    case 0:
      progress = data;
      break;
    case 1:
      itemsInQueue = data;
      break;
    default:
      break;
    }
  }

  @Override
  public int getNetworkUpdate(int id) {
    switch (id) {
    case 0:
      return progress;
    case 1:
      return itemsInQueue;
    default:
      return 0;
    }
  }

  @Override
  public int getNetworkUpdateCount() {
    return 2;
  }

  @Override
  public int getNetworkUpdateCheckInterval() {
    return 1;
  }

}
