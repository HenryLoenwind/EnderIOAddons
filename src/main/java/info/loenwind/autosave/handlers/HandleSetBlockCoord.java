package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.util.BlockCoord;

public class HandleSetBlockCoord implements IHandler<Set<BlockCoord>> {

  public HandleSetBlockCoord() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return false;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull Set<BlockCoord> object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (!object.isEmpty()) {
      int[] intArray = new int[object.size() * 3];
      int i = 0;
      for (BlockCoord bc : object) {
        intArray[i++] = bc.x;
        intArray[i++] = bc.y;
        intArray[i++] = bc.z;
      }
      nbt.setIntArray(name, intArray);
    }
    return true;
  }

  @Override
  public Set<BlockCoord> read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nullable Set<BlockCoord> object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object == null) {
      object = new HashSet<BlockCoord>();
    } else {
      object.clear();
    }
    if (nbt.hasKey(name)) {
      int[] intArray = nbt.getIntArray(name);
      int i = 0;
      while (i < intArray.length) {
        object.add(new BlockCoord(intArray[i++], intArray[i++], intArray[i++]));
      }
    }
    return object;
  }

}
