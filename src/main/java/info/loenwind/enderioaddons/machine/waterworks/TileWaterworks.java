package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;

public class TileWaterworks extends AbstractTileFramework implements IFrameworkMachine {


  public TileWaterworks() {
    super(new SlotDefinition(0, 18, 1));
  }

  @Override
  public String getMachineName() {
    return BlockWaterworks.ModObject_blockWaterworks.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    if (redstoneCheckPassed) {
      return true;
    } else {
      return false;
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
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
  }

  @Override
  public boolean hasTank(TankSlot tankSlot) {
    return true;
  }

  @Override
  public Fluid getTankFluid(TankSlot tankSlot) {
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
  public AbstractMachineBlock getSlotMachine(TankSlot tankSlot) {
    if (tankSlot == TankSlot.FRONT_LEFT) {
      return null;
    }
    return null;
  }

  @Override
  public String getControllerModelName() {
    return BlockWaterworks.blockWaterworks.getControllerModelName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

}
