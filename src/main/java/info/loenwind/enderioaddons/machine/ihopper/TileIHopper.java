package info.loenwind.enderioaddons.machine.ihopper;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileIHopper extends AbstractTileFramework implements IFrameworkMachine {

  public TileIHopper() {
    super(new SlotDefinition(6, 6, 1));
  }

  @Override
  public String getMachineName() {
    return BlockIHopper.ModObject_blockIHopper.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      return true; // TODO check if slot is managed by ghost slot
    } else if (slotDefinition.isOutputSlot(i)) {
      return false; // TODO see above
    } else {
      return false;
    }
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    if (rsCheckPassed) {
      // TODO do something;
      return true;
    } else {
      return false;
    }
  }

  int outputSlotNo(int no) {
    return no + slotDefinition.minOutputSlot;
  }

  @Nullable
  ItemStack outputSlot(int no) {
    return inventory[outputSlotNo(no)];
  }

  int inputSlotNo(int no) {
    return no + slotDefinition.minInputSlot - 1;
  }

  @Nullable
  ItemStack inputSlot(int no) {
    return inventory[inputSlotNo(no)];
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
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if (slotDefinition.isInputSlot(slot)) {
      // TODO do we need to do something here?
    }
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if (slotDefinition.isInputSlot(fromSlot)) {
      // TODO do we need to do something here?
    }
    return super.decrStackSize(fromSlot, amount);
  }

  @Override
  public boolean hasTank(@Nonnull TankSlot tankSlot) {
    // render no tanks (lower level)
    return false;
  }

  @Override
  public Fluid getTankFluid(@Nonnull TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
    case BACK_RIGHT:
    case FRONT_RIGHT:
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
    return BlockIHopper.blockIHopper.getControllerModelName();
  }

  @Override
  public boolean renderSlot(@Nonnull TankSlot tankSlot) {
    // render all slots (upper level) but the controller slot
    return tankSlot != TankSlot.FRONT_LEFT;
  }

  @Override
  public IIcon getSlotIcon(@Nonnull TankSlot tankSlot, int side) {
    return Blocks.hopper.getIcon(side, 0); // TODO we need a model here
  }

}
