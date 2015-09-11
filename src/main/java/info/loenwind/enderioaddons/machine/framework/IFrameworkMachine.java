package info.loenwind.enderioaddons.machine.framework;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public interface IFrameworkMachine {

  enum TankSlot {
    FRONT_LEFT, FRONT_RIGHT, BACK_RIGHT, BACK_LEFT;
  }

  boolean hasTank(@Nonnull TankSlot tankSlot);

  @Nullable
  Fluid getTankFluid(@Nonnull TankSlot tankSlot);

  boolean hasController();

  //  Block getSlotMachine(TankSlot tankSlot);
  //
  //  int getSlotMachineMeta(TankSlot tankSlot);

  boolean renderSlot(@Nonnull TankSlot tankSlot);

  @Nullable
  IIcon getSlotIcon(@Nonnull TankSlot tankSlot, int side);

  String getControllerModelName();
}
