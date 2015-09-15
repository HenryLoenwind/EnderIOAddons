package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.HandleStorable;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class Reader {

  public static <T> void read(Registry registry, Set<StoreFor> phase, NBTTagCompound tag, T object) {
    try {
      (new HandleStorable<T>()).toplevelRead(registry, phase, tag, object);
    } catch (IllegalAccessException | InstantiationException | NoHandlerFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> void read(Set<StoreFor> phase, NBTTagCompound tag, T object) {
    read(Registry.GLOBAL_REGISTRY, phase, tag, object);
  }

  public static <T> void read(Registry registry, StoreFor phase, NBTTagCompound tag, T object) {
    read(registry, EnumSet.of(phase), tag, object);
  }

  public static <T> void read(StoreFor phase, NBTTagCompound tag, T object) {
    read(Registry.GLOBAL_REGISTRY, EnumSet.of(phase), tag, object);
  }

  public static <T> void read(Registry registry, NBTTagCompound tag, T object) {
    read(registry, EnumSet.allOf(StoreFor.class), tag, object);
  }

  public static <T> void read(NBTTagCompound tag, T object) {
    read(Registry.GLOBAL_REGISTRY, EnumSet.allOf(StoreFor.class), tag, object);
  }

}
