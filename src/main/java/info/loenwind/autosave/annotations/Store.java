package info.loenwind.autosave.annotations;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.handlers.NullHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Store {

  public enum StoreFor {
    SAVE, CLIENT, ITEM;
  }

  public StoreFor[] value() default { StoreFor.SAVE, StoreFor.CLIENT, StoreFor.ITEM };

  public Class<? extends IHandler> handler() default NullHandler.class;
}
