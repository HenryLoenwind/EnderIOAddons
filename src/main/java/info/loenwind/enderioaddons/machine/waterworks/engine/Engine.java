package info.loenwind.enderioaddons.machine.waterworks.engine;

import static com.enderio.core.common.util.ItemUtil.doInsertItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Engine {
  static int level_reduction_factor = 10;
  static int num_levels = 5;
  private List<Water> levels = new ArrayList<Water>();
  
  public Engine(Water config) {
    Stash input = config;
    for (int i = 0; i < num_levels; i++) {
      input = computeLevel(input, config, i);
    }
  }
  
  private Stash computeLevel(Stash input, Water mats, int level) {
    Stash remains = new Water();
    Water used = new Water();
    
    remains.getContents().putAll(input.getContents());
    Collections.sort(mats.getMaterials());
    
    for (Material mat : mats.getMaterials()) {
      if (mat.getItem().getItemStack() != null) {
        boolean good2go = true;
        for (Component comp : mat.getComponents()) {
          Double available = remains.getContents().get(comp.getName());
          if (available == null || available < comp.getGranularity()) {
            good2go = false;
          }
        }
        if (good2go) {
          used.getMaterials().add(mat);
          while (good2go) {
            for (Component comp : mat.getComponents()) {
              Double needed = comp.getGranularity();
              Double available = remains.getContents().get(comp.getName());
              remains.getContents().put(comp.getName(), available - needed);
              if (used.getContents().get(comp.getName()) == null) {
                used.getContents().put(comp.getName(), needed * comp.getFactor());
              } else {
                used.getContents().put(comp.getName(), used.getContents().get(comp.getName()) + needed * comp.getFactor());
              }
              if (available < 2 * needed) {
                good2go = false;
              }
            }
          }
        }
      }
    }
    
    for (Entry<String, Double> content : remains.getContents().entrySet()) {
      content.setValue(content.getValue() * level_reduction_factor);
    }
    
    levels.add(level, used);
    return remains;
  }
  
  /*
   * Logic:
   * 
   * if (createItems(false) != NO_OUTPUTS) { useEnergy(); if (createItems(true)
   * in (NO_INPUTS, OK)) { if inputTank >= 1000 mB) { processWater();
   * useEnergy(); inputTank -= 1000 mB; outputTank += 100 mB; } }
   */
  
  public void processWater(Stash stash, int level) {
    Stash use = levels.get(level);
    for (Entry<String, Double> comp : use.getContents().entrySet()) {
      stash.getContents().put(comp.getKey(), stash.getContents().get(comp.getKey()) + comp.getValue());
    }
  }

  enum CreationResult {
    NO_INPUTS, OK, LOW_OUTPUTS, NO_OUTPUTS;
  }

  /**
   * Converts the internal buffer into items and adds them into an inventory.
   * 
   * 
   * @param stash
   *          The internal buffer to take the input from
   * @param level
   *          The level to work at
   * @param inv
   *          The inventory to (try to) put items
   * @param startSlot
   *          The first slot where items can be put
   * @param endSlot
   *          The last slot where items can be put
   * @param doCreate
   *          Whether to do a dry run or actually create the items
   * @return NO_INPUTS if there are not enough materials in the given stash to
   *         create any items. Ff there is not enough space in the target
   *         inventory it will return LOW_OUTPUTS or NO_OUTPUTS, with
   *         LOW_OUTPUTS meaning that at least one item was successfully
   *         created. If it created at least one item and could put all created
   *         items into the target, it'll return OK. Please note that in dry run
   *         mode LOW_OUTPUTS is not a possible return value. OK is returned
   *         instead.
   */
  public CreationResult createItems(Stash stash, int level, IInventory inv, int startSlot, int endSlot, boolean doCreate) {
    boolean haveInserted = false;
    Water use = levels.get(level);
    for (Material mat : use.getMaterials()) {
      
      // (1) compute how much mass we need to build one item
      double needed_mass = mat.getVolume() * mat.getDensity(); // cm³ * g/cm³ = g
      int parts = 0;
      for (Component comp : mat.getComponents()) {
        parts += comp.getCount();
      }
      double needed_mass_per_part = needed_mass / parts;

      boolean good2go = true;
      while (good2go) {
        // (2) check if there is enough for one item
        for (Component comp : mat.getComponents()) {
          if (stash.getContents().get(comp.getName()) < comp.getCount() * needed_mass_per_part) {
            good2go = false;
          }
        }
        // (3) if yes, try to add an item to the inventory
        if (good2go) {
          ItemStack item = mat.getItem().getItemStack();
          //          if (doInsertItem(inv, startSlot, endSlot, item, doCreate) > 0) {
          if (doInsertItem(inv, startSlot, endSlot, item) > 0) { // TODO: use line above once core catches up to PR#13
            if (!doCreate) {
              return CreationResult.OK;
            }
            haveInserted = true;
            for (Component comp : mat.getComponents()) {
              stash.getContents().put(comp.getName(), stash.getContents().get(comp.getName()) - comp.getCount() * needed_mass_per_part);
            }
          } else {
            // we cannot add this item, so stop processing
            return haveInserted ? CreationResult.LOW_OUTPUTS : CreationResult.NO_OUTPUTS;
          }
        }
      }
    }
    
    return haveInserted ? CreationResult.OK : CreationResult.NO_INPUTS;
  }

}
