package info.loenwind.enderioaddons.machine.tcom.engine;

import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.AXE;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.BOOTS;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.BOW;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.CHEST;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.HELMET;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.HOE;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.NONE;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.PANTS;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.PICK;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.SHOVEL;
import static info.loenwind.enderioaddons.machine.tcom.engine.ItemTypes.SWORD;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.COBBLE;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.DIAMOND;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.GOLD;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.IRON;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.LEATHER;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STICK;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STRING;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.WOOD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class Item2Mats {
  private static final Map<ItemTypes, Map<Item, List<Mats>>> all = new HashMap<>();
  private static final Set<Item> allItems = new HashSet<>();
  private static final Map<ItemTypes, Set<Item>> items = new HashMap<>();

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
    for (ItemTypes itemType : ItemTypes.values()) {
      all.put(itemType, new HashMap<Item, List<Mats>>());
      items.put(itemType, new HashSet<Item>());
    }
    all.get(BOW).put(Items.bow, matList(STICK, STICK, STICK, STRING, STRING, STRING));

    all.get(SWORD).put(Items.wooden_sword, matList(STICK, WOOD, WOOD));
    all.get(SWORD).put(Items.stone_sword, matList(STICK, COBBLE, COBBLE));
    all.get(SWORD).put(Items.iron_sword, matList(STICK, IRON, IRON));
    all.get(SWORD).put(Items.golden_sword, matList(STICK, GOLD, GOLD));
    all.get(SWORD).put(Items.diamond_sword, matList(STICK, DIAMOND, DIAMOND));

    all.get(PICK).put(Items.wooden_pickaxe, matList(STICK, STICK, WOOD, WOOD, WOOD));
    all.get(PICK).put(Items.stone_pickaxe, matList(STICK, STICK, COBBLE, COBBLE, COBBLE));
    all.get(PICK).put(Items.iron_pickaxe, matList(STICK, STICK, IRON, IRON, IRON));
    all.get(PICK).put(Items.golden_pickaxe, matList(STICK, STICK, GOLD, GOLD, GOLD));
    all.get(PICK).put(Items.diamond_pickaxe, matList(STICK, STICK, DIAMOND, DIAMOND, DIAMOND));

    all.get(SHOVEL).put(Items.wooden_shovel, matList(STICK, STICK, WOOD));
    all.get(SHOVEL).put(Items.stone_shovel, matList(STICK, STICK, COBBLE));
    all.get(SHOVEL).put(Items.iron_shovel, matList(STICK, STICK, IRON));
    all.get(SHOVEL).put(Items.golden_shovel, matList(STICK, STICK, GOLD));
    all.get(SHOVEL).put(Items.diamond_shovel, matList(STICK, STICK, DIAMOND));

    all.get(HOE).put(Items.wooden_hoe, matList(STICK, STICK, WOOD, WOOD));
    all.get(HOE).put(Items.stone_hoe, matList(STICK, STICK, COBBLE, COBBLE));
    all.get(HOE).put(Items.iron_hoe, matList(STICK, STICK, IRON, IRON));
    all.get(HOE).put(Items.golden_hoe, matList(STICK, STICK, GOLD, GOLD));
    all.get(HOE).put(Items.diamond_hoe, matList(STICK, STICK, DIAMOND, DIAMOND));

    all.get(AXE).put(Items.wooden_axe, matList(STICK, STICK, WOOD, WOOD, WOOD));
    all.get(AXE).put(Items.stone_axe, matList(STICK, STICK, COBBLE, COBBLE, COBBLE));
    all.get(AXE).put(Items.iron_axe, matList(STICK, STICK, IRON, IRON, IRON));
    all.get(AXE).put(Items.golden_axe, matList(STICK, STICK, GOLD, GOLD, GOLD));
    all.get(AXE).put(Items.diamond_axe, matList(STICK, STICK, DIAMOND, DIAMOND, DIAMOND));

    all.get(BOOTS).put(Items.leather_boots, matList(4, LEATHER));
    all.get(BOOTS).put(Items.iron_boots, matList(4, IRON));
    all.get(BOOTS).put(Items.golden_boots, matList(4, GOLD));
    all.get(BOOTS).put(Items.diamond_boots, matList(4, DIAMOND));

    all.get(PANTS).put(Items.leather_leggings, matList(7, LEATHER));
    all.get(PANTS).put(Items.iron_leggings, matList(7, IRON));
    all.get(PANTS).put(Items.golden_leggings, matList(7, GOLD));
    all.get(PANTS).put(Items.diamond_leggings, matList(7, DIAMOND));

    all.get(CHEST).put(Items.leather_chestplate, matList(8, LEATHER));
    all.get(CHEST).put(Items.iron_chestplate, matList(8, IRON));
    all.get(CHEST).put(Items.golden_chestplate, matList(8, GOLD));
    all.get(CHEST).put(Items.diamond_chestplate, matList(8, DIAMOND));

    all.get(HELMET).put(Items.leather_helmet, matList(5, LEATHER));
    all.get(HELMET).put(Items.iron_helmet, matList(5, IRON));
    all.get(HELMET).put(Items.golden_helmet, matList(5, GOLD));
    all.get(HELMET).put(Items.diamond_helmet, matList(5, DIAMOND));

    for (ItemTypes itemType : ItemTypes.values()) {
      allItems.addAll(all.get(itemType).keySet());
      items.get(itemType).addAll(all.get(itemType).keySet());
    }
  }

  public static boolean isValid(Item item) {
    return allItems.contains(item);
  }

  public static boolean isValid(ItemStack itemStack) {
    Item item = itemStack.getItem();
    return item != null && allItems.contains(item);
  }

  public static boolean isValid(ItemTypes itemType, Item item) {
    return items.get(itemType).contains(item);
  }

  public static boolean isValid(ItemTypes itemType, ItemStack itemStack) {
    Item item = itemStack.getItem();
    return item != null && items.get(itemType).contains(item);
  }

  public static List<Mats> getMats(ItemTypes itemType, Item item) {
    return all.get(itemType).get(item);
  }

  public static List<Mats> getMats(ItemTypes itemType, ItemStack itemStack) {
    Item item = itemStack.getItem();
    if (item != null && all.get(itemType).containsKey(item)) {
      return all.get(itemType).get(item);
    } else {
      return Collections.EMPTY_LIST;
    }
  }

  public static ItemTypes getItemType(Item item) {
    for (ItemTypes itemType : ItemTypes.values()) {
      if (items.get(itemType).contains(item)) {
        return itemType;
      }
    }
    return NONE;
  }

  public static ItemTypes getItemType(ItemStack itemStack) {
    Item item = itemStack.getItem();
    if (item != null) {
      return getItemType(item);
    }
    return NONE;
  }

}