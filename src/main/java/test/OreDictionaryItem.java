package test;

import org.xml.sax.Attributes;

import com.enderio.core.common.util.OreDictionaryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Log;
import crazypants.enderio.machine.recipe.OreDictionaryRecipeInput;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.material.OreDictionaryPreferences;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryItem implements Item {
  String oreDictionary;
  private ItemStack stack = null;

  // modID="EnderIO" itemName="itemAlloy" itemMeta="1"
  // oreDictionary="dustCoal"
  
  /* (non-Javadoc)
   * @see test.Item#getItemStack()
   */
  @Override
  public ItemStack getItemStack() {
    if (stack == null && oreDictionary != null) {
      stack = OreDictionaryPreferences.instance.getPreferred(oreDictionary);
      if (stack != null) {
        stack = stack.copy();
        stack.stackSize = 1;
      } else {
        oreDictionary = null;
      }
    }

    return stack;
  }
}
