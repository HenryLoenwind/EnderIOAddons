package info.loenwind.autosave.annotations;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.handlers.HandleStorable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Storable {

  public Class<? extends IHandler> handler() default HandleStorable.class;

}
