package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.tool.SmartTank;

public class HandleSmartTank implements IHandler<SmartTank> {

  public HandleSmartTank() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return SmartTank.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, SmartTank object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    object.writeCommon(name, nbt);
    return true;
  }

  @Override
  public SmartTank read(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, SmartTank object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      object.readCommon(name, nbt);
    }
    return object;
  }

}
