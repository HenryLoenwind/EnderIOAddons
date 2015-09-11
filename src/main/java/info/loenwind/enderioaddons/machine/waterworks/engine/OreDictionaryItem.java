package info.loenwind.enderioaddons.machine.waterworks.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import crazypants.enderio.material.OreDictionaryPreferences;

public class OreDictionaryItem implements Item {
  @Nullable
  private String oreDictionary;
  @Nullable
  private ItemStack stack = null;

  // modID="EnderIO" itemName="itemAlloy" itemMeta="1"
  // oreDictionary="dustCoal"
  
  public OreDictionaryItem(@Nonnull String oreDictionary) {
    this.oreDictionary = oreDictionary;
  }

  @Override
  @Nullable
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
