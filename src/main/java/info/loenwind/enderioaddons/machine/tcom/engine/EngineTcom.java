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
  private final float[] enchantmentPool = new float[Enchantment.enchantmentsList.length];
  @Store
  private final float[] materialPool = new float[Mats.values().length];

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
      materialPool[mat.ordinal()] += 1f * factor;
    }
    for (Entry<Integer, Integer> enchantment : enchantments.entrySet()) {
      enchantmentPool[enchantment.getKey()] += enchantment.getValue() * factor;
    }
    return true;
  }

  public Map<Enchantment, Float> getEnchantments() {
    Map<Enchantment, Float> result = new HashMap<>();
    for (int i = 0; i < enchantmentPool.length; i++) {
      if (enchantmentPool[i] > .1f) {
        result.put(Enchantment.enchantmentsList[i], enchantmentPool[i]);
      }
    }
    return result;
  }

  public Map<ItemStack, Float> getMaterials() {
    Map<ItemStack, Float> result = new HashMap<>();
    for (int i = 0; i < materialPool.length; i++) {
      if (materialPool[i] > .1f) {
        result.put(Mats.values()[i].getItemStack(), materialPool[i]);
      }
    }
    return result;
  }

  public float getAmount(Mats mat) {
    return materialPool[mat.ordinal()];
  }

  public boolean canGet(Mats mat) {
    return materialPool[mat.ordinal()] >= 1;
  }

  public boolean get(Mats mat) {
    if (materialPool[mat.ordinal()] >= 1) {
      materialPool[mat.ordinal()] -= 1;
      return true;
    } else {
      return false;
    }
  }

  private EnchantmentData getEnchantmentData(int id) {
    Enchantment enchantment = Enchantment.enchantmentsList[id];
    final int maxLevel = enchantment.getMaxLevel();
    int level = LinearRandom.getValue((int) (maxLevel < enchantmentPool[id] ? maxLevel : enchantmentPool[id]), rand);
    enchantmentPool[id] -= level;
    return new EnchantmentData(enchantment, level);
  }

  public boolean isValidTarget(ItemStack itemStack) {
    if (itemStack == null || itemStack.getItem() == null || itemStack.getItem() == Items.enchanted_book || itemStack.stackSize != 1) {
      return false;
    }
    Map<Integer, Integer> enchantmentsOnItemStack = EnchantmentHelper.getEnchantments(itemStack);
    for (int id = 0; id < enchantmentPool.length; id++) {
      if (enchantmentPool[id] >= 1) {
        Enchantment enchantmentInPool = Enchantment.enchantmentsList[id];
        if (itemStack.getItem() == Items.book) {
          if (enchantmentInPool.isAllowedOnBooks()) {
            return true;
          }
        } else {
          if (enchantmentsOnItemStack.isEmpty()) {
            if (enchantmentInPool.canApply(itemStack)) {
              return true;
            }
          } else {
            for (Integer id2 : enchantmentsOnItemStack.keySet()) {
              Enchantment enchantmentOnItemStack = Enchantment.enchantmentsList[id2];
              if (enchantmentInPool.canApply(itemStack) && enchantmentInPool.canApplyTogether(enchantmentOnItemStack)
                  && enchantmentOnItemStack.canApplyTogether(enchantmentInPool)) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }

  public Map<Enchantment, Float> getEnchantments(ItemStack itemStack) {
    if (itemStack == null) {
      return getEnchantments();
    }
    if (itemStack.getItem() == null || itemStack.getItem() == Items.enchanted_book || itemStack.stackSize != 1) {
      return Collections.EMPTY_MAP;
    }
    Map<Enchantment, Float> result = new HashMap<>();
    Map<Integer, Integer> enchantmentsOnItemStack = EnchantmentHelper.getEnchantments(itemStack);
    for (int id = 0; id < enchantmentPool.length; id++) {
      if (enchantmentPool[id] >= 1) {
        Enchantment enchantmentInPool = Enchantment.enchantmentsList[id];
        if (itemStack.getItem() == Items.book) {
          if (enchantmentInPool.isAllowedOnBooks()) {
            result.put(enchantmentInPool, enchantmentPool[id]);
          }
        } else {
          if (enchantmentsOnItemStack.isEmpty()) {
            if (enchantmentInPool.canApply(itemStack)) {
              result.put(enchantmentInPool, enchantmentPool[id]);
            }
          } else {
            for (Integer id2 : enchantmentsOnItemStack.keySet()) {
              Enchantment enchantmentOnItemStack = Enchantment.enchantmentsList[id2];
              if (enchantmentInPool.canApply(itemStack) && enchantmentInPool.canApplyTogether(enchantmentOnItemStack)
                  && enchantmentOnItemStack.canApplyTogether(enchantmentInPool)) {
                result.put(enchantmentInPool, enchantmentPool[id]);
              }
            }
          }
        }
      }
    }
    return result;
  }

  public float getEnchantmentAmounts() {
    float result = 0;
    for (int id = 0; id < enchantmentPool.length; id++) {
      result += enchantmentPool[id];
    }
    return result;
  }

  public boolean addEnchantment(ItemStack itemStack, int id) {
    Enchantment enchantmentInPool = Enchantment.enchantmentsList[id];
    if (itemStack == null || itemStack.getItem() == null || itemStack.getItem() == Items.enchanted_book || itemStack.stackSize != 1
        || enchantmentInPool == null || enchantmentPool[id] < 1) {
      return false;
    }
    if (itemStack.getItem() == Items.book) {
      if (!enchantmentInPool.isAllowedOnBooks()) {
        return false;
      }
      EnchantmentData enchantmentData = getEnchantmentData(id);
      itemStack.func_150996_a(Items.enchanted_book);
      Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
      return true;
    } else {
      if (!enchantmentInPool.canApply(itemStack)) {
        return false;
      }
      Map<Integer, Integer> enchantmentsOnItemStack = EnchantmentHelper.getEnchantments(itemStack);
      for (Integer id2 : enchantmentsOnItemStack.keySet()) {
        Enchantment enchantmentOnItemStack = Enchantment.enchantmentsList[id2];
        if (!enchantmentInPool.canApplyTogether(enchantmentOnItemStack) || !enchantmentOnItemStack.canApplyTogether(enchantmentInPool)) {
          return false;
        }
      }
      EnchantmentData enchantmentData = getEnchantmentData(id);
      itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
      return true;
    }
  }

}
