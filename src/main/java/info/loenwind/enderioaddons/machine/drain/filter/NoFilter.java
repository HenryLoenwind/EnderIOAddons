package info.loenwind.enderioaddons.machine.drain.filter;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class NoFilter implements FluidFilter {

  private final boolean value;

  public NoFilter(boolean value) {
    this.value = value;
  }

  @Override
  public boolean isFluid(FluidStack fluid) {
    return value;
  }

  @Override
  public boolean isFluid(Fluid fluid) {
    return value;
  }

}
