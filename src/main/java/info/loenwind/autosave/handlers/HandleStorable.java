package info.loenwind.autosave.handlers;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class HandleStorable<T extends Object> implements IHandler<T> {

  public HandleStorable() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    Storable annotation = clazz.getAnnotation(info.loenwind.autosave.annotations.Storable.class);
    return annotation != null && annotation.handler() == this.getClass();
  }

  @Override
  public boolean store(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, T object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    toplevelStore(registry, phase, tag, object);
    nbt.setTag(name, tag);
    return true;
  }

  private static boolean containsAny(Set<StoreFor> a, StoreFor[] b) {
    for (StoreFor storeFor : b) {
      if (a.contains(storeFor)) {
        return true;
      }
    }
    return false;
  }

  public void toplevelStore(Registry registry, Set<StoreFor> phase, NBTTagCompound tag, T object) throws IllegalAccessException, InstantiationException,
      NoHandlerFoundException {
    for (Field field : object.getClass().getDeclaredFields()) {
      Store annotation = field.getAnnotation(Store.class);
      if (annotation != null && containsAny(phase, annotation.value())) {
        field.setAccessible(true);
        Object fieldData = field.get(object);
        if (fieldData != null) {
          Class<?> fieldType = field.getType();
          boolean done = false;
          boolean foundHandler = false;
          if (annotation.handler() != NullHandler.class) {
            done = annotation.handler().newInstance().store(registry, phase, tag, field.getName(), fieldData);
            foundHandler = true;
          }
          if (!done) {
            List<IHandler> handlers = registry.findHandlers(fieldType);
            for (IHandler handler : handlers) {
              if (!done) {
                done = handler.store(registry, phase, tag, field.getName(), fieldData);
                foundHandler = true;
              }
            }
          }
          if (!foundHandler) {
            throw new NoHandlerFoundException(field, object);
          }
        } else {
          tag.setBoolean(field.getName() + "__null", true);
        }
      }
    }
  }

  @Override
  public T read(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, T object) throws IllegalArgumentException,
      IllegalAccessException,
      InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      NBTTagCompound tag = nbt.getCompoundTag(name);
      toplevelRead(registry, phase, tag, object);
    }
    return object;
  }

  public void toplevelRead(Registry registry, Set<StoreFor> phase, NBTTagCompound tag, T object) throws IllegalAccessException, InstantiationException,
      NoHandlerFoundException {
    for (Field field : object.getClass().getDeclaredFields()) {
      Store annotation = field.getAnnotation(Store.class);
      if (annotation != null && containsAny(phase, annotation.value())) {
        field.setAccessible(true);
        if (!tag.hasKey(field.getName() + "__null")) {
          Object fieldData = field.get(object);
          Class<?> fieldType = field.getType();
          Object done = null;
          boolean foundHandler = false;
          if (annotation.handler() != NullHandler.class) {
            done = annotation.handler().newInstance().read(registry, phase, tag, field.getName(), fieldData);
            foundHandler = true;
          }
          if (done == null) {
            List<IHandler> handlers = registry.findHandlers(fieldType);
            for (IHandler handler : handlers) {
              if (done == null) {
                done = handler.read(registry, phase, tag, field.getName(), fieldData);
                foundHandler = true;
              }
            }
          }
          if (!foundHandler) {
            throw new NoHandlerFoundException(field, object);
          } else {
            field.set(object, done);
          }
        } else {
          field.set(object, null);
        }
      }
    }
  }
}
