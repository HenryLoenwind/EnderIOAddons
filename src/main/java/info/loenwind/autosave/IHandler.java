package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public interface IHandler<T> {

  boolean canHandle(Class<?> clazz);

  /**
   * Stores an object in a NBT structure.
   * 
   * @param nbt
   *          The NBT to put the data in
   * @param object
   *          The object to store
   * @return True if the object was completely handled. False otherwise. In that
   *         case, the next matching handler will be called. Please note that it
   *         <i>is</i> ok to partially handle a object and return false. This
   *         can be used to handle inheritance.
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws NoHandlerFoundException
   */
  boolean store(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, T object) throws IllegalArgumentException,
      IllegalAccessException,
      InstantiationException, NoHandlerFoundException;

  /**
   * Reads an object from a NBT structure
   * 
   * @param nbt
   *          The NBT to read the data from
   * @param object
   *          The existing object into which the data should be read, or which
   *          should be replaced by a new object with the read data. The
   *          decision which of those two possibilities to execute is open to
   *          the implementation.
   * @return The object that should be placed into the field. This may be the
   *         changed parameter object or a new one. If this returns null, the
   *         next matching handler will be given a chance to execute. Please
   *         note that <i>is</i> it ok to change the input object in this case.
   *         This can be used to implement inheritance.
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws NoHandlerFoundException
   */
  T read(Registry registry, Set<Store.StoreFor> phase, NBTTagCompound nbt, String name, T object) throws IllegalArgumentException, IllegalAccessException,
      InstantiationException, NoHandlerFoundException;
}
