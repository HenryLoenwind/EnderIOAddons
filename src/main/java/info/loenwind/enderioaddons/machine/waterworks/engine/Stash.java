package info.loenwind.enderioaddons.machine.waterworks.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

public class Stash {

  @Nonnull
  private final Map<String, Double> contents = new HashMap<String, Double>();

  public Stash() {
  }

  @Nonnull
  public Map<String, Double> getContents() {
    return contents;
  }

  public void readFromNbt(NBTTagCompound nbtRoot) {
    contents.clear();
    Set<String> keySet = nbtRoot.func_150296_c();
    for (String key : keySet) {
      contents.put(key, nbtRoot.getDouble(key));
    }
  }

  public void writeToNbt(NBTTagCompound nbtRoot) {
    for (Entry<String, Double> entry : contents.entrySet()) {
      nbtRoot.setDouble(entry.getKey(), entry.getValue());
    }
  }

}