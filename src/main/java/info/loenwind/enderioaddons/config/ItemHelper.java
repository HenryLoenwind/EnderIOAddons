package info.loenwind.enderioaddons.config;

import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.machine.waterworks.engine.MinecraftItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.Pair;

public class ItemHelper {

  private static final Pattern p = Pattern.compile("^\\s*([^:\\s]+):([^@\\s]+)(?:@(-?\\d+))?\\s*(?:=\\s*(\\d+)\\s*)?$");

  public static List<WeightedItemStack> readWeightedList(String input) {
    if (input == null || input.trim().isEmpty()) {
      return Collections.<WeightedItemStack> emptyList();
    }
    List<WeightedItemStack> result = new ArrayList<>();
    for (String item : input.split(",|;")) {
      if (item != null && !item.trim().isEmpty()) {
        final Matcher matcher = p.matcher(item);
        if (matcher.matches()) {
          String modid = matcher.group(1);
          String itemid = matcher.group(2);
          String metaS = matcher.group(3);
          String weightS = matcher.group(4);

          try {
            if (modid == null || itemid == null) {
              throw new NumberFormatException();
            }
            int meta = metaS == null ? 0 : Integer.valueOf(metaS);
            if (meta == -1) {
              meta = OreDictionary.WILDCARD_VALUE;
            } else if (meta < 0 || meta >= 4096) {
              throw new NumberFormatException();
            }
            int weight = weightS == null ? 1 : Integer.valueOf(weightS);

            MinecraftItem mci = new MinecraftItem(modid, itemid, meta);
            ItemStack stack = mci.getItemStack();
            if (stack != null) {
              result.add(new WeightedItemStack(weight, stack));
            } else {
              Log.info("Could not find item '" + modid + ":" + itemid + "@" + meta + "', ignoring");
            }
          } catch (NumberFormatException e) {
            Log.error("Error while reading config. '" + item + "' is not a valid item specifier");
          }
        } else {
          Log.error("Error while reading config. '" + item + "' is not a valid item specifier");
        }

      }
    }

    if (result.isEmpty()) {
      Log.error("Error while reading config. '" + input + "' gave zero valid items.");
    }

    return result;
  }

  private static final Map<Config, Pair<String, List<WeightedItemStack>>> cache = new HashMap<>();

  public static List<WeightedItemStack> readWeightedList(Config config) {
    if (cache.containsKey(config)) {
      Pair<String, List<WeightedItemStack>> pair = cache.get(config);
      if (pair.getLeft().equals(config.getString())) {
        return pair.getRight();
      }
    }
    List<WeightedItemStack> result = readWeightedList(config.getString());
    cache.put(config, Pair.of(config.getString(), result));
    return result;
  }

  public static List<ItemStack> readList(Config config) {
    List<WeightedItemStack> weightedList = readWeightedList(config);
    if (weightedList == null) {
      return null;
    }
    List<ItemStack> result = new ArrayList<>();
    for (WeightedItemStack weightedItemStack : weightedList) {
      result.add(weightedItemStack.getStack());
    }
    return result;
  }

}
