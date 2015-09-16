package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.HandleStorable;
import info.loenwind.enderioaddons.common.NullHelper;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public class Reader {

  public static <T> void read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object) {
    try {
      (new HandleStorable<T>()).toplevelRead(registry, phase, tag, object);
    } catch (IllegalAccessException | InstantiationException | NoHandlerFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> void read(@Nullable Set<Store.StoreFor> phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnull(phase, "Missing phase"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  public static <T> void read(@Nonnull Registry registry, @Nonnull StoreFor phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(registry, NullHelper.notnull(EnumSet.of(phase), "Java broke"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  public static <T> void read(@Nonnull StoreFor phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnull(EnumSet.of(phase), "Java broke"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  public static <T> void read(@Nonnull Registry registry, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(registry, NullHelper.notnull(EnumSet.allOf(StoreFor.class), "Java broke"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  public static <T> void read(@Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnull(EnumSet.allOf(StoreFor.class), "Java broke"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

}
