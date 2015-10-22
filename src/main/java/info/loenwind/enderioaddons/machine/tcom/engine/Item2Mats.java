package info.loenwind.enderioaddons.machine.tcom.engine;

import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.COBBLE;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.DARKSTEEL;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.DIAMOND;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.GOLD;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.IRON;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.LEATHER;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STICK;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STRING;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.WOOD;
import info.loenwind.enderioaddons.recipe.Recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class Item2Mats {

  private static final Map<Item, List<Mats>> allItems = new HashMap<>();

  private static List<Mats> matList(Mats... mats) {
    List<Mats> result = new ArrayList<>();
    for (Mats mat : mats) {
      result.add(mat);
    }
    return result;
  }

  private static List<Mats> matList(int amount, Mats mat) {
    List<Mats> result = new ArrayList<>();
    for (int i = 1; i <= amount; i++) {
      result.add(mat);
    }
    return result;
  }

  static {
    allItems.put(Items.bow, matList(STICK, STICK, STICK, STRING, STRING, STRING));

    allItems.put(Items.fishing_rod, matList(STICK, STICK, STICK, STRING, STRING));

    allItems.put(Items.wooden_sword, matList(STICK, WOOD, WOOD));
    allItems.put(Items.stone_sword, matList(STICK, COBBLE, COBBLE));
    allItems.put(Items.iron_sword, matList(STICK, IRON, IRON));
    allItems.put(Items.golden_sword, matList(STICK, GOLD, GOLD));
    allItems.put(Items.diamond_sword, matList(STICK, DIAMOND, DIAMOND));
    allItems.put(Recipes.darkSteel_sword.getItem(), matList(STICK, DARKSTEEL, DARKSTEEL));

    allItems.put(Items.wooden_pickaxe, matList(STICK, STICK, WOOD, WOOD, WOOD));
    allItems.put(Items.stone_pickaxe, matList(STICK, STICK, COBBLE, COBBLE, COBBLE));
    allItems.put(Items.iron_pickaxe, matList(STICK, STICK, IRON, IRON, IRON));
    allItems.put(Items.golden_pickaxe, matList(STICK, STICK, GOLD, GOLD, GOLD));
    allItems.put(Items.diamond_pickaxe, matList(STICK, STICK, DIAMOND, DIAMOND, DIAMOND));
    allItems.put(Recipes.darkSteel_pickaxe.getItem(), matList(STICK, STICK, DARKSTEEL, DARKSTEEL, DARKSTEEL));

    allItems.put(Items.wooden_shovel, matList(STICK, STICK, WOOD));
    allItems.put(Items.stone_shovel, matList(STICK, STICK, COBBLE));
    allItems.put(Items.iron_shovel, matList(STICK, STICK, IRON));
    allItems.put(Items.golden_shovel, matList(STICK, STICK, GOLD));
    allItems.put(Items.diamond_shovel, matList(STICK, STICK, DIAMOND));

    allItems.put(Items.wooden_hoe, matList(STICK, STICK, WOOD, WOOD));
    allItems.put(Items.stone_hoe, matList(STICK, STICK, COBBLE, COBBLE));
    allItems.put(Items.iron_hoe, matList(STICK, STICK, IRON, IRON));
    allItems.put(Items.golden_hoe, matList(STICK, STICK, GOLD, GOLD));
    allItems.put(Items.diamond_hoe, matList(STICK, STICK, DIAMOND, DIAMOND));

    allItems.put(Items.wooden_axe, matList(STICK, STICK, WOOD, WOOD, WOOD));
    allItems.put(Items.stone_axe, matList(STICK, STICK, COBBLE, COBBLE, COBBLE));
    allItems.put(Items.iron_axe, matList(STICK, STICK, IRON, IRON, IRON));
    allItems.put(Items.golden_axe, matList(STICK, STICK, GOLD, GOLD, GOLD));
    allItems.put(Items.diamond_axe, matList(STICK, STICK, DIAMOND, DIAMOND, DIAMOND));
    allItems.put(Recipes.darkSteel_axe.getItem(), matList(STICK, STICK, DARKSTEEL, DARKSTEEL, DARKSTEEL));

    allItems.put(Items.leather_boots, matList(4, LEATHER));
    allItems.put(Items.iron_boots, matList(4, IRON));
    allItems.put(Items.golden_boots, matList(4, GOLD));
    allItems.put(Items.diamond_boots, matList(4, DIAMOND));
    allItems.put(Recipes.darkSteel_boots.getItem(), matList(4, DARKSTEEL));

    allItems.put(Items.leather_leggings, matList(7, LEATHER));
    allItems.put(Items.iron_leggings, matList(7, IRON));
    allItems.put(Items.golden_leggings, matList(7, GOLD));
    allItems.put(Items.diamond_leggings, matList(7, DIAMOND));
    allItems.put(Recipes.darkSteel_leggings.getItem(), matList(7, DARKSTEEL));

    allItems.put(Items.leather_chestplate, matList(8, LEATHER));
    allItems.put(Items.iron_chestplate, matList(8, IRON));
    allItems.put(Items.golden_chestplate, matList(8, GOLD));
    allItems.put(Items.diamond_chestplate, matList(8, DIAMOND));
    allItems.put(Recipes.darkSteel_chestplate.getItem(), matList(8, DARKSTEEL));

    allItems.put(Items.leather_helmet, matList(5, LEATHER));
    allItems.put(Items.iron_helmet, matList(5, IRON));
    allItems.put(Items.golden_helmet, matList(5, GOLD));
    allItems.put(Items.diamond_helmet, matList(5, DIAMOND));
    allItems.put(Recipes.darkSteel_helmet.getItem(), matList(5, DARKSTEEL));

  }

  public static boolean isValid(Item item) {
    return allItems.containsKey(item);
  }

  public static boolean isValid(ItemStack itemStack) {
    Item item = itemStack.getItem();
    return item != null && allItems.containsKey(item);
  }

  public static List<Mats> getMats(ItemStack itemStack) {
    Item item = itemStack.getItem();
    if (item != null && allItems.containsKey(item)) {
      return allItems.get(item);
    } else {
      return Collections.EMPTY_LIST;
    }
  }

}