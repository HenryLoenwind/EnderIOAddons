package info.loenwind.autosave.handlers.enderioaddons;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.enderioaddons.machine.pmon.StatCollector;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class HandleStatCollector implements IHandler<StatCollector> {

  public HandleStatCollector() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return StatCollector.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, StatCollector object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setByteArray("data", object.getData());
    tag.setInteger("collectCount", object.getCollectCount());
    tag.setInteger("pos", object.getPos());
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public StatCollector read(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, StatCollector object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object != null && nbt.hasKey(name)) {
      NBTTagCompound tag = nbt.getCompoundTag(name);
      object.setData(tag.getByteArray("data"));
      object.setCollectCount(tag.getInteger("collectCount"));
      object.setPos(tag.getInteger("pos"));
    }
    return object;
  }

}
