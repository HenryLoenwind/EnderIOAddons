package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class HandleFloat implements IHandler<Float> {

  public HandleFloat() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, Float object) throws IllegalArgumentException,
      IllegalAccessException {
    nbt.setFloat(name, object);
    return true;
  }

  @Override
  public Float read(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, Float object) {
    return nbt.hasKey(name) ? nbt.getFloat(name) : object != null ? object : 0f;
  }

}
