package info.loenwind.enderioaddons.machine.drain.filter;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface FluidFilter {

  boolean isFluid(FluidStack fluid);

  boolean isFluid(Fluid fluid);

}