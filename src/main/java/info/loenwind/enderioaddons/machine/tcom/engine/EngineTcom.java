package info.loenwind.enderioaddons.machine.tcom.engine;

import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.NONE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class EngineTcom {

  private float[] enchantAmounts = new float[Enchantment.enchantmentsList.length];
  private float[] materialAmounts = new float[Mats.values().length];
  private ItemTypes itemType = NONE;

  private final float loss;

  public EngineTcom(float loss) {
    this.loss = loss;
  }

  public boolean addable(ItemStack itemStack) {
    if (itemType == NONE) {
      return Item2Mats.isValid(itemStack);
    } else {
      return Item2Mats.isValid(itemType, itemStack);
    }
  }

  public boolean add(ItemStack itemStack) {
    if (itemType == NONE) {
      itemType = Item2Mats.getItemType(itemStack);
      if (itemType == NONE) {
        return false;
      }
    } else if (itemType != Item2Mats.getItemType(itemStack)) {
      return false;
    }
    //
    List<Mats> mats = Item2Mats.getMats(itemType, itemStack);
    int itemDamage = itemStack.getItemDamage();
    int maxDamage = itemStack.getMaxDamage();
    int stackSize = itemStack.stackSize;
    Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
    float factor = (maxDamage - itemDamage) / maxDamage * stackSize * (1f - loss);
    for (Mats mat : mats) {
      materialAmounts[mat.ordinal()] += 1f * factor;
    }
    for (Entry<Integer, Integer> enchantment : enchantments.entrySet()) {
      enchantAmounts[enchantment.getKey()] += enchantment.getValue() * factor;
    }
    return true;
  }

  public Map<Enchantment, Float> getEnchantments() {
    Map<Enchantment, Float> result = new HashMap<>();
    for (int i = 0; i < enchantAmounts.length; i++) {
      if (enchantAmounts[i] > .1f) {
        result.put(Enchantment.enchantmentsList[i], enchantAmounts[i]);
      }
    }
    return result;
  }

  public Map<ItemStack, Float> getMaterials() {
    Map<ItemStack, Float> result = new HashMap<>();
    for (int i = 0; i < materialAmounts.length; i++) {
      if (materialAmounts[i] > .1f) {
        result.put(Mats.values()[i].getItemStack(), materialAmounts[i]);
      }
    }
    return result;
  }

  public boolean canGet(Mats mat) {
    return materialAmounts[mat.ordinal()] >= 1;
  }

  public boolean get(Mats mat) {
    if (materialAmounts[mat.ordinal()] >= 1) {
      materialAmounts[mat.ordinal()] -= 1;
      return true;
    } else {
      return false;
    }
  }

}
