package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.HandleStorable;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class Writer {

  public static <T> void write(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound tag, T object) {
    try {
      (new HandleStorable<T>()).toplevelStore(registry, phase, tag, object);
    } catch (IllegalAccessException | InstantiationException | NoHandlerFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> void write(Set<Store.StoreFor> phase, NBTTagCompound tag, T object) {
    write(Registry.GLOBAL_REGISTRY, phase, tag, object);
  }

  public static <T> void write(Registry registry, StoreFor phase, NBTTagCompound tag, T object) {
    write(registry, EnumSet.of(phase), tag, object);
  }

  public static <T> void write(StoreFor phase, NBTTagCompound tag, T object) {
    write(Registry.GLOBAL_REGISTRY, EnumSet.of(phase), tag, object);
  }

  public static <T> void write(Registry registry, NBTTagCompound tag, T object) {
    write(registry, EnumSet.allOf(StoreFor.class), tag, object);
  }

  public static <T> void write(NBTTagCompound tag, T object) {
    write(Registry.GLOBAL_REGISTRY, EnumSet.allOf(StoreFor.class), tag, object);
  }

}
