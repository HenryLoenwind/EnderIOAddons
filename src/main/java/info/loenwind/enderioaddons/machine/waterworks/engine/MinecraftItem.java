package info.loenwind.enderioaddons.machine.waterworks.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class MinecraftItem implements info.loenwind.enderioaddons.machine.waterworks.engine.Item {
  @Nonnull
  private final String modID;
  @Nullable
  private String itemName;
  private final int itemMeta;
  @Nullable
  private ItemStack stack = null;

  // modID="EnderIO" itemName="itemAlloy" itemMeta="1"
  // oreDictionary="dustCoal"
  
  public MinecraftItem(@Nonnull String modID, @Nonnull String itemName, int itemMeta) {
    this.modID = modID;
    this.itemName = itemName;
    this.itemMeta = itemMeta;
  }

  @Override
  @Nullable
  public ItemStack getItemStack() {
    if (stack == null && itemName != null) {
      Item i = GameRegistry.findItem(modID, itemName);
      if (i != null) {
        stack = new ItemStack(i, 1, itemMeta);
      } else {
        Block b = GameRegistry.findBlock(modID, itemName);
        if (b != null) {
          stack = new ItemStack(b, 1, itemMeta);
        } else {
          itemName = null;
        }
      }
    }

    return stack;
  }

}
