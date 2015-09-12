package info.loenwind.enderioaddons.machine.waterworks.engine;

import static com.enderio.core.common.util.ItemUtil.doInsertItem;
import info.loenwind.enderioaddons.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Engine {
  private static final int num_levels = 5;
  @Nonnull
  private final List<Water> levels = new ArrayList<Water>();
  private double waterWorksWaterReductionPercentageUsedInCalcs = 0.0;
  @Nonnull
  private final Water config;
  
  public Engine(@Nonnull Water config) {
    this.config = config;
    computeLevels();
  }

  private void computeLevels() {
    if (waterWorksWaterReductionPercentageUsedInCalcs != Config.waterWorksWaterReductionPercentage) {
      waterWorksWaterReductionPercentageUsedInCalcs = Config.waterWorksWaterReductionPercentage;
      levels.clear();
      Stash input = config;
      for (int i = 0; i < num_levels; i++) {
        input = computeLevel(input, i);
      }
    }
  }

  @Nonnull
  private Stash computeLevel(@Nonnull Stash input, int level) {
    Stash remains = new Stash();
    Water used = new Water();
    
    remains.getContents().putAll(input.getContents());
    Collections.sort(config.getMaterials());
    
    for (Material mat : config.getMaterials()) {
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
      content.setValue(content.getValue() * 100.0 / Config.waterWorksWaterReductionPercentage);
    }
    
    levels.add(level, used);
    return remains;
  }
  
  public void processWater(@Nonnull Stash stash, int level, double factor) {
    computeLevels();
    Stash use = levels.get(level);
    for (Entry<String, Double> comp : use.getContents().entrySet()) {
      stash.getContents().put(comp.getKey(),
          (stash.getContents().containsKey(comp.getKey()) ? stash.getContents().get(comp.getKey()) : 0) + comp.getValue() * factor);
    }
  }

  public enum CreationResult {
    NO_INPUTS, OK, LOW_OUTPUTS, NO_OUTPUTS;
  }

  private double progress = 0.0;

  public double getLastProgress() {
    return progress;
  }

  /**
   * Converts the internal buffer into items and adds them into an inventory.
   * 
   * 
   * @param stash
   *          The internal buffer to take the input from
   * @param inv
   *          The inventory to (try to) put items
   * @param startSlot
   *          The first slot where items can be put
   * @param endSlot
   *          The last slot where items can be put
   * @param doCreate
   *          Whether to do a dry run or actually create the items
   * @return NO_INPUTS if there are not enough materials in the given stash to
   *         create any items. If there is not enough space in the target
   *         inventory it will return LOW_OUTPUTS or NO_OUTPUTS, with
   *         LOW_OUTPUTS meaning that at least one item was successfully
   *         created. If it created at least one item and could put all created
   *         items into the target, it'll return OK. Please note that in dry run
   *         mode LOW_OUTPUTS is not a possible return value. OK is returned
   *         instead.
   */
  @Nonnull
  public CreationResult createItems(@Nonnull Stash stash, int level, @Nonnull IInventory inv, int startSlot, int endSlot, boolean doCreate) {
    computeLevels();
    boolean haveInserted = false;
    progress = 0.0;
    for (Material mat : levels.get(level).getMaterials()) {
      
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
        double all_needed = 0.0, all_there = 0.0;
        for (Component comp : mat.getComponents()) {
          double needed = comp.getCount() * needed_mass_per_part;
          all_needed += needed;
          double there = stash.getContents().containsKey(comp.getName()) ? stash.getContents().get(comp.getName()) : 0.0;
          all_there += there;
          if (there < needed) {
            good2go = false;
          }
        }
        // (3) if yes, try to add an item to the inventory
        if (good2go) {
          ItemStack item = mat.getItem().getItemStack();
          if (doInsertItem(inv, startSlot, endSlot, item, doCreate) > 0) {
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
        } else if (all_needed > 0.0) {
          double this_progress = all_there / all_needed;
          if (this_progress > progress) {
            progress = this_progress;
          }
        }
      }
    }
    
    return haveInserted ? CreationResult.OK : CreationResult.NO_INPUTS;
  }

}
