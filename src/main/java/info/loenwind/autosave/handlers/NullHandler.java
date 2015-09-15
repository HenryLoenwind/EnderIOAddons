package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

// Dummy handler
public class NullHandler implements IHandler<NullHandler> {

  private NullHandler() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return false;
  }

  @Override
  public boolean store(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, NullHandler object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return false;
  }

  @Override
  public NullHandler read(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, NullHandler object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return null;
  }

}
