package test;

import org.xml.sax.Attributes;

import com.enderio.core.common.util.OreDictionaryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Log;
import crazypants.enderio.machine.recipe.OreDictionaryRecipeInput;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.material.OreDictionaryPreferences;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MinecraftItem implements test.Item {
  String modID;
  String itemName;
  int itemMeta;
  private ItemStack stack = null;

  // modID="EnderIO" itemName="itemAlloy" itemMeta="1"
  // oreDictionary="dustCoal"
  
  /* (non-Javadoc)
   * @see test.Item#getItemStack()
   */
  @Override
  public ItemStack getItemStack() {
    if (stack == null && itemName != null && modID != null) {
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
