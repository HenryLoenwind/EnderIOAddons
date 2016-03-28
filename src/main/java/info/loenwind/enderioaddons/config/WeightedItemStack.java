package info.loenwind.enderioaddons.config;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedItemStack extends WeightedRandom.Item {

  private final ItemStack stack;

  public WeightedItemStack(int weight, ItemStack stack) {
    super(weight);
    this.stack = stack;
  }

  @Override
  public String toString() {
    return "WeightedItemStack [weight=" + itemWeight + ", stack=" + getStack() + "]";
  }

  public ItemStack getStack() {
    return stack;
  }

}