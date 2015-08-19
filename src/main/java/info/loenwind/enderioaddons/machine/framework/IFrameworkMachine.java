package info.loenwind.enderioaddons.machine.framework;

import net.minecraftforge.fluids.Fluid;
import crazypants.enderio.machine.AbstractMachineBlock;

public interface IFrameworkMachine {

  enum TankSlot {
    FRONT_LEFT, FRONT_RIGHT, BACK_RIGHT, BACK_LEFT;
  }

  boolean hasTank(TankSlot tankSlot);

  Fluid getTankFluid(TankSlot tankSlot);

  boolean hasController();

  AbstractMachineBlock getSlotMachine(TankSlot tankSlot);

  String getControllerModelName();
}
