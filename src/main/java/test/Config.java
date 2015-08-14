package test;

import static com.enderio.core.common.util.ItemUtil.doInsertItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Config {
  static int level_reduction_factor = 10;
  static int num_levels = 5;
  private List<Water> levels = new ArrayList<Water>();
  
  Config(Water config) {
    Water input = config;
    for (int i = 0; i < num_levels; i++) {
      input = computeLevel(input, config, i);
    }
  }
  
  boolean testing = true;
  
  private Water computeLevel(Water input, Water mats, int level) {
    Water remains = new Water();
    Water used = new Water();
    
    remains.contents.putAll(input.contents);
    Collections.sort(mats.materials);
    
    for (Material mat : mats.materials) {
      if (testing || mat.item.getItemStack() != null) {
        boolean good2go = true;
        for (Component comp : mat.components) {
          Double available = remains.contents.get(comp.name);
          if (available == null || available < comp.granularity) {
            good2go = false;
          }
        }
        if (good2go) {
          used.materials.add(mat);
          while (good2go) {
            for (Component comp : mat.components) {
              Double needed = comp.granularity;
              Double available = remains.contents.get(comp.name);
              remains.contents.put(comp.name, available - needed);
              if (used.contents.get(comp.name) == null) {
                used.contents.put(comp.name, needed * comp.factor);
              } else {
                used.contents.put(comp.name, used.contents.get(comp.name) + needed * comp.factor);
              }
              if (available < 2 * needed) {
                good2go = false;
              }
            }
          }
        }
      }
    }
    
    for (Entry<String, Double> content : remains.contents.entrySet()) {
      content.setValue(content.getValue() * level_reduction_factor);
    }
    
    levels.add(level, used);
    return remains;
  }
  
  /*
   * Logic:
   * 
   * if (createItems() && inputTank >= 1000 mB) { processWater(); inputTank -=
   * 1000 mB; outputTank += 100 mB; }
   */
  
  void processWater(Water stash, int level) {
    Water use = levels.get(level);
    for (Entry<String, Double> comp : use.contents.entrySet()) {
      stash.contents.put(comp.getKey(), stash.contents.get(comp.getKey()) + comp.getValue());
    }
  }

  boolean createItems(Water stash, int level, IInventory inv, int startSlot, int endSlot) {
    Water use = levels.get(level);
    for (Material mat : use.materials) {
      
      // (1) compute how much mass we need to build one item
      double needed_mass = mat.volume * mat.density; // cm³ * g/cm³ = g
      int parts = 0;
      for (Component comp : mat.components) {
        parts += comp.count;
      }
      double needed_mass_per_part = needed_mass / parts;

      boolean good2go = true;
      while (good2go) {
        // (2) check if there is enough for one item
        for (Component comp : mat.components) {
          if (stash.contents.get(comp.name) < comp.count * needed_mass_per_part) {
            good2go = false;
          }
        }
        // (3) if yes, try to add an item to the inventory
        if (good2go) {
          ItemStack item = mat.item.getItemStack();
          if (doInsertItem(inv, startSlot, endSlot, item) > 0) {
            for (Component comp : mat.components) {
              stash.contents.put(comp.name, stash.contents.get(comp.name) - comp.count * needed_mass_per_part);
            }
          } else {
            // we cannot add this item, so stop processing
            return false;
          }
        }
      }
    }
    
    return true;
  }

}
