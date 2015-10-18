package info.loenwind.enderioaddons.machine.tcom;

import static crazypants.enderio.config.Config.powerConduitTierOneRF;
import static crazypants.enderio.config.Config.powerConduitTierThreeRF;
import static crazypants.enderio.config.Config.powerConduitTierTwoRF;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.tcom.engine.EngineTcom;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileTcom extends AbstractTileFramework implements IFrameworkMachine {

  @Store
  protected EngineTcom engine = new EngineTcom(.1f); // TODO config

  public TileTcom() {
    super(new SlotDefinition(2, 2, 1));
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
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    if (rsCheckPassed && shouldDoWorkThisTick(10)) { // TODO cfg
      if (inventory[slotDefinition.getMinInputSlot()] != null && canUsePower(100f)) { // TODO cfg
        if (engine.add(inventory[slotDefinition.getMinInputSlot()])) {
          inventory[slotDefinition.getMinInputSlot()] = null;
          markDirty();
        }
      }
    }
    return false;
  }

  //  private void playSound() {
  //    if (crazypants.enderio.config.Config.machineSoundsEnabled) {
  //      // TODO      getWorldObj().playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, EnderIOAddons.DOMAIN + ":machine.ihopper", getVolume(), getPitch());
  //    }
  //  }

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
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR: // TODO power values
      setCapacitor(new BasicCapacitor(powerConduitTierOneRF, 100000, powerConduitTierOneRF));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(powerConduitTierTwoRF, 200000, powerConduitTierTwoRF));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(powerConduitTierThreeRF, 500000, powerConduitTierThreeRF));
      break;
    }
  }

  @Override
  public boolean hasTank(@Nonnull TankSlot tankSlot) {
    return tankSlot == TankSlot.FRONT_RIGHT;
  }

  @Override
  public Fluid getTankFluid(@Nonnull TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
    case BACK_RIGHT:
    case FRONT_RIGHT:
      return engine.getEnchantmentAmounts() > 1f ? EnderIO.fluidXpJuice : null;
    case BACK_LEFT:
    }
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

}
