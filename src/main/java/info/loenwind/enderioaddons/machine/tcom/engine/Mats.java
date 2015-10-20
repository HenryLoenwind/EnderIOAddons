package info.loenwind.enderioaddons.machine.tcom.engine;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum Mats {
  STICK(Items.stick), STRING(Items.string), LEATHER(Items.leather), WOOD(Blocks.planks), COBBLE(Blocks.cobblestone), IRON(Items.iron_ingot), GOLD(
      Items.gold_ingot), DIAMOND(Items.diamond);

  private final ItemStack itemStack;

  private Mats(Item item) {
    this.itemStack = new ItemStack(item);
  }

  private Mats(Block block) {
    this.itemStack = new ItemStack(block);
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public static Mats getMat(ItemStack itemStack) {
    if (itemStack != null && itemStack.getItem() != null) {
      for (Mats mat : values()) {
        if (mat.itemStack.getItem() == itemStack.getItem()) {
          return mat;
        }
      }
    }
    return null;
  }

  public boolean isSame(ItemStack itemstack) {
    return itemstack != null && itemstack.getItem() != null && this.itemStack.getItem() == itemstack.getItem();
  }
}