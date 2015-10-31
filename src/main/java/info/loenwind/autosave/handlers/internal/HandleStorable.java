package info.loenwind.autosave.handlers.internal;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.common.NullHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

/**
 * An {@link IHandler} that can (re-)store objects by storing their fields. The
 * fields to (re-)store must be annotated {@link Store}.
 * <p>
 * It will also process the annotated fields of superclasses, as long as there
 * is an unbroken chain of {@link Storable} annotations (without special
 * handlers). Fields that have the same name as a field in a sub-/super-class
 * will be processed independently.
 * <p>
 * If the final superclass has an {@link IHandler} registered in the
 * {@link Registry}, it will also be processed. However, this will <i>not</i>
 * work for handlers that return a new object instead of changing the given one.
 * A handler can check for this case by seeing if its "name" parameter is
 * {@link #SUPERCLASS_KEY}.
 *
 * @param <T>
 */
public class HandleStorable<T extends Object> implements IHandler<T> {

  protected static final @Nonnull String NULL_POSTFIX = "__null";
  public static final @Nonnull String SUPERCLASS_KEY = "__superclass";
  private static final @Nonnull Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
  private static final @Nonnull Map<Field, Set<StoreFor>> phaseCache = new HashMap<>();
  private static final @Nonnull Map<Field, List<IHandler>> fieldHandlerCache = new HashMap<>();
  private static final @Nonnull Map<Class<?>, Class<?>> superclassCache = new HashMap<>();
  private static final @Nonnull Map<Class<?>, List<IHandler>> superclassHandlerCache = new HashMap<>();

  public HandleStorable() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    Storable annotation = clazz.getAnnotation(info.loenwind.autosave.annotations.Storable.class);
    return annotation != null && annotation.handler() == this.getClass();
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull T object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    toplevelStore(registry, phase, tag, object);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public T read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nullable T object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      NBTTagCompound tag = NullHelper.notnullM(nbt.getCompoundTag(name), "NBTTagCompound.getCompoundTag()");
      toplevelRead(registry, phase, tag, object);
    }
    return object;
  }

  public void toplevelStore(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object)
      throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    Class<? extends Object> clazz = object.getClass();
    cacheHandlers(registry, clazz);

    for (Field field : fieldCache.get(clazz)) {
      final Set<StoreFor> fieldphases = phaseCache.get(field);
      if (fieldphases == null || field == null) {
        Log.warn("Data error in NBT handling: class=" + clazz + " field=" + field + " phases=" + fieldphases
            + ". Please report this at https://github.com/HenryLoenwind/EnderIOAddons/issues");
      } else if (!Collections.disjoint(fieldphases, phase)) {
        Object fieldData = field.get(object);
        String fieldName = field.getName();
        if (fieldData != null && fieldName != null) {
          for (IHandler handler : fieldHandlerCache.get(field)) {
            if (handler.store(registry, phase, tag, fieldName, fieldData)) {
              break;
            }
          }
        } else {
          tag.setBoolean(fieldName + NULL_POSTFIX, true);
        }
      }
    }

    Class<?> superclazz = superclassCache.get(clazz);
    if (superclazz != null) {
      for (IHandler handler : superclassHandlerCache.get(superclazz)) {
        if (handler.store(registry, phase, tag, SUPERCLASS_KEY, object)) {
          break;
        }
      }
    }
  }

  public void toplevelRead(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object)
      throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    Class<? extends Object> clazz = object.getClass();
    cacheHandlers(registry, clazz);

    for (Field field : fieldCache.get(clazz)) {
      final Set<StoreFor> fieldphases = phaseCache.get(field);
      if (fieldphases == null || field == null) {
        Log.warn("Data error in NBT handling: class=" + clazz + " field=" + field + " phases=" + fieldphases
            + ". Please report this at https://github.com/HenryLoenwind/EnderIOAddons/issues");
      } else if (!Collections.disjoint(fieldphases, phase)) {
        Object fieldData = field.get(object);
        String fieldName = field.getName();
        if (!tag.hasKey(fieldName + NULL_POSTFIX) && fieldName != null) {
          for (IHandler handler : fieldHandlerCache.get(field)) {
            Object result = handler.read(registry, phase, tag, fieldName, fieldData);
            if (result != null) {
              field.set(object, result);
              break;
            }
          }
        } else {
          field.set(object, null);
        }
      }
    }

    Class<?> superclazz = superclassCache.get(clazz);
    if (superclazz != null) {
      for (IHandler handler : superclassHandlerCache.get(superclazz)) {
        if (handler.read(registry, phase, tag, SUPERCLASS_KEY, object) != null) {
          break;
        }
      }
    }

  }

  public void cacheHandlers(@Nonnull Registry registry, Class<?> clazz) throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (fieldCache.containsKey(clazz)) {
      return;
    }

    final ArrayList<Field> fieldList = new ArrayList<Field>();
    for (Field field : clazz.getDeclaredFields()) {
      Store annotation = field.getAnnotation(Store.class);
      if (annotation != null) {
        ArrayList<IHandler> handlerList = new ArrayList<IHandler>();
        String fieldName = field.getName();
        if (fieldName != null) {
          Class<?> fieldType = field.getType();
          if (annotation.handler() != NullHandler.class) {
            handlerList.add(annotation.handler().newInstance());
          }
          handlerList.addAll(registry.findHandlers(fieldType));
          if (handlerList.isEmpty()) {
            throw new NoHandlerFoundException(field, clazz);
          }
          EnumSet<StoreFor> enumSet = EnumSet.noneOf(StoreFor.class);
          enumSet.addAll(Arrays.asList(annotation.value()));
          phaseCache.put(field, enumSet);
          field.setAccessible(true);
          fieldList.add(field);
          fieldHandlerCache.put(field, handlerList);
        }
      }
    }

    Class<?> superclazz = clazz.getSuperclass();
    if (superclazz != null) {
      Storable annotation = superclazz.getAnnotation(Storable.class);
      if (annotation != null) {
        if (annotation.handler() == this.getClass()) {
          cacheHandlers(registry, superclazz);
          fieldList.addAll(fieldCache.get(superclazz));
        } else {
          superclassCache.put(clazz, superclazz);
          if (!superclassCache.containsKey(superclazz)) {
            superclassHandlerCache.put(superclazz, (List<IHandler>) Arrays.asList(annotation.handler().newInstance()));
          }
        }
      } else {
        List<IHandler> handlers = registry.findHandlers(superclazz);
        if (!handlers.isEmpty()) {
          superclassCache.put(clazz, superclazz);
          if (!superclassCache.containsKey(superclazz)) {
            superclassHandlerCache.put(superclazz, handlers);
          }
        }
      }
    }

    fieldCache.put(clazz, fieldList);
  }
}
