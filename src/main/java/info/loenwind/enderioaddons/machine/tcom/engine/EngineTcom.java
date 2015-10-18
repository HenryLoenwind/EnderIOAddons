package info.loenwind.enderioaddons.machine.tcom.engine;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Storable
public class EngineTcom {

  private static final Random rand = new Random();

  @Store
  private final float[] enchantAmounts = new float[Enchantment.enchantmentsList.length];
  @Store
  private final float[] materialAmounts = new float[Mats.values().length];

  private final float loss;

  public EngineTcom(float loss) {
    this.loss = loss;
  }

  @SuppressWarnings("static-method")
  public boolean addable(ItemStack itemStack) {
    if (itemStack == null || itemStack.getItem() == null || itemStack.stackSize != 1) {
      return false;
    }
    return Item2Mats.isValid(itemStack);
  }

  public boolean add(ItemStack itemStack) {
    List<Mats> mats = Item2Mats.getMats(Item2Mats.getItemType(itemStack), itemStack);
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

  public float getAmount(Mats mat) {
    return materialAmounts[mat.ordinal()];
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

  private EnchantmentData getEnchantmentData(int id) {
    Enchantment enchantment = Enchantment.enchantmentsList[id];
    final int maxLevel = enchantment.getMaxLevel();
    int level = LinearRandom.getValue((int) (maxLevel < enchantAmounts[id] ? maxLevel : enchantAmounts[id]), rand);
    enchantAmounts[id] -= level;
    return new EnchantmentData(enchantment, level);
  }

  public boolean isValidTarget(ItemStack itemStack) {
    if (itemStack == null || itemStack.getItem() == null || itemStack.getItem() == Items.enchanted_book || itemStack.stackSize != 1) {
      return false;
    }
    Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
    for (int id = 0; id < enchantAmounts.length; id++) {
      if (enchantAmounts[id] >= 1) {
        Enchantment enchantment = Enchantment.enchantmentsList[id];
        if (itemStack.getItem() == Items.book) {
          if (enchantment.isAllowedOnBooks()) {
            return true;
          }
        } else {
          for (Integer id2 : enchantments.keySet()) {
            Enchantment enchantment2 = Enchantment.enchantmentsList[id2];
            if (enchantment.canApplyTogether(enchantment2) && enchantment2.canApplyTogether(enchantment)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public Map<Enchantment, Float> getEnchantments(ItemStack itemStack) {
    if (itemStack == null || itemStack.getItem() == null || itemStack.getItem() == Items.enchanted_book || itemStack.stackSize != 1) {
      return Collections.EMPTY_MAP;
    }
    Map<Enchantment, Float> result = new HashMap<>();
    Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
    for (int id = 0; id < enchantAmounts.length; id++) {
      if (enchantAmounts[id] >= 1) {
        Enchantment enchantment = Enchantment.enchantmentsList[id];
        if (itemStack.getItem() == Items.book) {
          if (enchantment.isAllowedOnBooks()) {
            result.put(Enchantment.enchantmentsList[id], enchantAmounts[id]);
          }
        } else {
          for (Integer id2 : enchantments.keySet()) {
            Enchantment enchantment2 = Enchantment.enchantmentsList[id2];
            if (enchantment.canApplyTogether(enchantment2) && enchantment2.canApplyTogether(enchantment)) {
              result.put(Enchantment.enchantmentsList[id], enchantAmounts[id]);
            }
          }
        }
      }
    }
    return result;
  }

  public float getEnchantmentAmounts() {
    float result = 0;
    for (int id = 0; id < enchantAmounts.length; id++) {
      result += enchantAmounts[id];
    }
    return result;
  }

  public boolean addEnchantment(ItemStack itemStack, int id) {
    Enchantment enchantment = Enchantment.enchantmentsList[id];
    if (itemStack == null || itemStack.getItem() == null || itemStack.getItem() == Items.enchanted_book || itemStack.stackSize != 1 || enchantment == null
        || enchantAmounts[id] < 1) {
      return false;
    }
    if (itemStack.getItem() == Items.book) {
      if (!enchantment.isAllowedOnBooks()) {
        return false;
      }
      EnchantmentData enchantmentData = getEnchantmentData(id);
      itemStack.func_150996_a(Items.enchanted_book);
      Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
      return true;
    } else {
      Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
      if (!enchantment.canApply(itemStack)) {
        return false;
      }
      for (Integer id2 : enchantments.keySet()) {
        Enchantment enchantment2 = Enchantment.enchantmentsList[id2];
        if (!enchantment.canApplyTogether(enchantment2) || !enchantment2.canApplyTogether(enchantment)) {
          return false;
        }
      }
      EnchantmentData enchantmentData = getEnchantmentData(id);
      itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
      return true;
    }
  }

}
