package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class HandleFluid implements IHandler<Fluid> {

  public HandleFluid() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Fluid.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, Fluid object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setString(name, FluidRegistry.getFluidName(object));
    return true;
  }

  @Override
  public Fluid read(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, Fluid object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return nbt.hasKey(name) ? FluidRegistry.getFluid(nbt.getString(name)) : object;
  }

}
