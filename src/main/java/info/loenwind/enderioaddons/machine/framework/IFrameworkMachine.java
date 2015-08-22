package info.loenwind.enderioaddons.machine.framework;

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public interface IFrameworkMachine {

  enum TankSlot {
    FRONT_LEFT, FRONT_RIGHT, BACK_RIGHT, BACK_LEFT;
  }

  boolean hasTank(TankSlot tankSlot);

  Fluid getTankFluid(TankSlot tankSlot);

  boolean hasController();

  //  Block getSlotMachine(TankSlot tankSlot);
  //
  //  int getSlotMachineMeta(TankSlot tankSlot);

  boolean renderSlot(TankSlot tankSlot);

  IIcon getSlotIcon(TankSlot tankSlot, int side);

  String getControllerModelName();
}
