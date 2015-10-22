package info.loenwind.enderioaddons.machine.tcom.engine;

import info.loenwind.enderioaddons.recipe.Recipes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum Mats {
  STICK(Items.stick, Blocks.planks), STRING(Items.string, null), LEATHER(Items.leather, null), WOOD(Blocks.planks), COBBLE(Blocks.cobblestone), IRON(
      Items.iron_ingot, Blocks.iron_block), GOLD(Items.gold_ingot, Blocks.gold_block), DIAMOND(Items.diamond, Blocks.diamond_block), DARKSTEEL(
      Recipes.darkSteel, Recipes.darkSteelBlock);

  private final ItemStack itemStack;
  private final ItemStack blockStack;

  private Mats(ItemStack itemStack, ItemStack blockStack) {
    this.itemStack = itemStack;
    this.blockStack = blockStack;
  }

  private Mats(Item item, Block block) {
    this.itemStack = new ItemStack(item);
    this.blockStack = new ItemStack(block);
  }

  private Mats(Block block) {
    this.itemStack = new ItemStack(block);
    this.blockStack = itemStack;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public ItemStack getBlockStack() {
    return blockStack;
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
    return itemstack != null && itemstack.getItem() != null && this.itemStack.getItem() == itemstack.getItem()
        && this.itemStack.getItemDamage() == itemstack.getItemDamage() && !itemstack.hasTagCompound();
  }
}