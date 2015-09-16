package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.handlers.HandleBlockCoord;
import info.loenwind.autosave.handlers.HandleFloat;
import info.loenwind.autosave.handlers.HandleFluid;
import info.loenwind.autosave.handlers.HandleFluidStack;
import info.loenwind.autosave.handlers.HandleInteger;
import info.loenwind.autosave.handlers.HandleItem;
import info.loenwind.autosave.handlers.HandleItemStack;
import info.loenwind.autosave.handlers.HandleSmartTank;
import info.loenwind.autosave.handlers.HandleStash;
import info.loenwind.autosave.handlers.HandleStorable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Registry {

  @Nonnull
  public static final Registry GLOBAL_REGISTRY = new Registry(true);

  static {
    // TODO: move to an eioa registry
    GLOBAL_REGISTRY.register(new HandleStash());

    // TODO: move to an eio registry
    GLOBAL_REGISTRY.register(new HandleSmartTank());

    // TODO: move to an ec registry
    GLOBAL_REGISTRY.register(new HandleBlockCoord());

    // Java primitives
    GLOBAL_REGISTRY.register(new HandleFloat());
    GLOBAL_REGISTRY.register(new HandleInteger());

    // Forge basic types
    GLOBAL_REGISTRY.register(new HandleFluidStack());
    GLOBAL_REGISTRY.register(new HandleFluid());

    // Minecraft basic types
    GLOBAL_REGISTRY.register(new HandleItemStack());
    GLOBAL_REGISTRY.register(new HandleItem());

    // Annotated objects
    GLOBAL_REGISTRY.register(new HandleStorable());
  }

  @Nonnull
  private final List<IHandler> handlers = new ArrayList<>();
  @Nullable
  private final Registry parent;

  private Registry(@SuppressWarnings("unused") boolean root) {
    parent = null;
  }

  public Registry() {
    this(GLOBAL_REGISTRY);
  }

  public Registry(@Nonnull Registry parent) {
    this.parent = parent;
  }

  public void register(@Nonnull IHandler handler) {
    handlers.add(handler);
  }

  @Nullable
  public IHandler findHandler(Class<?> clazz) throws InstantiationException, IllegalAccessException {
    for (IHandler handler : handlers) {
      if (handler.canHandle(clazz)) {
        return handler;
      }
    }
    final Registry thisParent = parent;
    if (thisParent != null) {
      return thisParent.findHandler(clazz);
    } else {
      Storable annotation = clazz.getAnnotation(Storable.class);
      if (annotation != null && annotation.handler() != HandleStorable.class) {
        return annotation.handler().newInstance();
      }
    }
    return null;
  }

  @Nonnull
  public List<IHandler> findHandlers(Class<?> clazz) throws InstantiationException, IllegalAccessException {
    List<IHandler> result = new ArrayList<>();
    for (IHandler handler : handlers) {
      if (handler.canHandle(clazz)) {
        result.add(handler);
      }
    }
    final Registry thisParent = parent;
    if (thisParent != null) {
      result.addAll(thisParent.findHandlers(clazz));
    } else {
      Storable annotation = clazz.getAnnotation(Storable.class);
      while (annotation != null && annotation.handler() != HandleStorable.class) {
        result.add(annotation.handler().newInstance());
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
          annotation = superclass.getAnnotation(Storable.class);
        }
      }
    }
    return result;
  }

}
