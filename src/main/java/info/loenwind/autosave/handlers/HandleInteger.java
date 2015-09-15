package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class HandleInteger implements IHandler<Integer> {

  public HandleInteger() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, Integer object) throws IllegalArgumentException,
      IllegalAccessException {
    nbt.setInteger(name, object);
    return true;
  }

  @Override
  public Integer read(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, Integer object) {
    return nbt.hasKey(name) ? nbt.getInteger(name) : object != null ? object : 0;
  }

}
