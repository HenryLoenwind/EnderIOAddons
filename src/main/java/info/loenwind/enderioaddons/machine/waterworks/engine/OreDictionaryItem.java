package info.loenwind.enderioaddons.machine.waterworks.engine;

import net.minecraft.item.ItemStack;
import crazypants.enderio.material.OreDictionaryPreferences;

public class OreDictionaryItem implements Item {
  private String oreDictionary;
  private ItemStack stack = null;

  // modID="EnderIO" itemName="itemAlloy" itemMeta="1"
  // oreDictionary="dustCoal"
  
  public OreDictionaryItem(String oreDictionary) {
    this.oreDictionary = oreDictionary;
  }

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
