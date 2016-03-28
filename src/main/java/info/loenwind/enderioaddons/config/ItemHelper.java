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

import org.apache.commons.lang3.tuple.Pair;

public class ItemHelper {

  private static final Pattern p = Pattern.compile("^\\s*([^:\\s]+):([^@\\s]+)(?:@(\\d+))?\\s*=\\s*(\\d+)\\s*$");

  public static List<WeightedItemStack> readWeightedList(String input) {
    if (input == null || input.trim().isEmpty()) {
      return Collections.<WeightedItemStack> emptyList();
    }
    List<WeightedItemStack> result = new ArrayList<>();
    for (String item : input.split(",|;")) {
      if (item != null && !item.trim().isEmpty()) {
        final Matcher matcher = p.matcher(item);
        if (matcher.matches()) {
          //          System.out.println(matcher.groupCount());
          //          for (int i = 0; i <= matcher.groupCount(); i++) {
          //            System.out.println("Group " + i + ": >" + matcher.group(i) + "<");
          //          }
          String modid = matcher.group(1);
          String itemid = matcher.group(2);
          String metaS = matcher.group(3);
          String weightS = matcher.group(4);

          try {
            if (modid == null || itemid == null || weightS == null) {
              throw new NumberFormatException();
            }
            int meta = metaS == null ? 0 : Integer.valueOf(metaS);
            if (meta < 0 || meta >= 4096) {
              throw new NumberFormatException();
            }
            int weight = Integer.valueOf(weightS);

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

  public static void main(String[] args) {
    readWeightedList(",minecraft:dirt@1=99,;, enderio:whatever@0 = 47   ,other:what=13,");
    String a = "enderioaddons:itemMachineParts@23=13500, EnderIO:itemBasicCapacitor@0=10, EnderIO:itemBasicCapacitor@1=5, EnderIO:itemBasicCapacitor@2=1";

    String b = "enderioaddons:itemMachineParts@23=10000, enderioaddons:itemMachineParts@24=3000, enderioaddons:itemMachineParts@25=500, "
        + "EnderIO:itemBasicCapacitor@0=10, EnderIO:itemBasicCapacitor@1=5, EnderIO:itemBasicCapacitor@2=1";
    info.loenwind.enderioaddons.config.ItemHelper.readWeightedList(info.loenwind.enderioaddons.config.Config.plantDropsLowGain);
    readWeightedList(a);
    readWeightedList(b);
  }
}
