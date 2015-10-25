package info.loenwind.enderioaddons.machine.magcharger;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.flag.ItemFlag;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.api.common.util.IProgressTile;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileMagCharger extends AbstractTileFramework implements IProgressTile {

  protected @Store int progress = 0; // in ticks, counting down

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
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      return itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemFlag && itemstack.getItemDamage() != 0;
    } else {
      return false;
    }
  }

  @Override
  public boolean isActive() {
    return progress > 0;
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    if (rsCheckPassed) {
      if (progress == 0
          && inventory[slotDefinition.getMinInputSlot()] != null
          && (inventory[slotDefinition.getMinOutputSlot()] == null || inventory[slotDefinition.getMinOutputSlot()].stackSize < inventory[slotDefinition
              .getMinOutputSlot()].getMaxStackSize())) {
        progress = 20; // TODO cfg
        inventory[slotDefinition.getMinInputSlot()].stackSize--;
        if (inventory[slotDefinition.getMinInputSlot()].stackSize <= 0) {
          inventory[slotDefinition.getMinInputSlot()] = null;
        }
        markDirty();
        playSound_in();
        return true;
      } else if (progress > 1 && usePower(10)) { // TODO cfg
        progress--;
      } else if (progress == 1) {
        if (inventory[slotDefinition.getMinOutputSlot()] == null) {
          inventory[slotDefinition.getMinOutputSlot()] = new ItemStack(BlockFlag.blockFlag, 1, 0);
          usePower(10);
          progress = 0;
          return true;
        } else if (inventory[slotDefinition.getMinOutputSlot()].stackSize < inventory[slotDefinition.getMinOutputSlot()].getMaxStackSize()) {
          inventory[slotDefinition.getMinOutputSlot()].stackSize++;
          usePower(10);
          progress = 0;
          return true;
        }
      }
    }
    return false;
  }

  private static final String[] soundsIds = { "machine.magcharger" };
  private static final int[] soundDurations = { 9999999 };
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

  public boolean canUsePower(Float wantToUse) {
    int w = wantToUse.intValue();
    return !((w < 1 ? 1 : w) > getEnergyStored());
  }

  public boolean canUsePower(int wantToUse) {
    return !(wantToUse > getEnergyStored());
  }

  public boolean usePower(Float wantToUse) {
    int w = wantToUse.intValue();
    return usePower(w < 1 ? 1 : w);
  }

  public boolean usePower(int wantToUse) {
    if (wantToUse > getEnergyStored()) {
      return false;
    } else {
      setEnergyStored(getEnergyStored() - wantToUse);
      return true;
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    // TODO
    setCapacitor(new BasicCapacitor(Config.tcomPowerIntakePerTick.getInt(), 10000, Config.tcomEnchantingPowerPerTick.getInt()
        + Config.tcomRecyclingPowerPerTick.getInt()));
  }

  @Override
  public float getProgress() {
    return (20 - progress) / 20f; // TODO cfg
  }

  @Override
  public void setProgress(float progress) {
    this.progress = (int) (progress * 20); // TODO cfg
  }

  @Override
  public TileEntity getTileEntity() {
    return this;
  }

}
