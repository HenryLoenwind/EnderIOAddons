package info.loenwind.enderioaddons.machine.drain.filter;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.tool.SmartTank;

public class TankFilter implements FluidFilter {

  private final FluidStack tankfluid;

  public TankFilter(SmartTank tank) {
    tankfluid = tank.getFluid();
  }

  @Override
  public boolean isFluid(FluidStack fluid) {
    return tankfluid.isFluidEqual(fluid);
  }

  @Override
  public boolean isFluid(Fluid fluid) {
    return tankfluid.getFluid() == fluid;
  }

}
