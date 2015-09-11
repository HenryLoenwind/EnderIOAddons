package info.loenwind.enderioaddons.machine.waterworks.engine;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface Item {

  @Nullable
  public abstract ItemStack getItemStack();

}