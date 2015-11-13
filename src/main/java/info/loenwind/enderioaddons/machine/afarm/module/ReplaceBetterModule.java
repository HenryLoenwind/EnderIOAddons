package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;

public class ReplaceBetterModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (!workTile.doDestroy && workTile.isAnalyzed) {
      if (workTile.seed != null
          && workTile.stats != null
          && workTile.stats.getGain() + workTile.stats.getGrowth() + workTile.stats.getStrength() < workTile.stats.getMaxGain() + workTile.stats.getMaxGrowth()
              + workTile.stats.getMaxStrength()) {
        int seedQuality = workTile.stats.getGain() + workTile.stats.getGrowth() + workTile.stats.getStrength();
        int betterSeed = hasBetterSeed(workTile, workTile.seed, seedQuality);
        if (betterSeed != -1) {
          workTile.cropsSlot = getCropsSticks(workTile);
          if (workTile.cropsSlot != -1) {
            workTile.farm.notifications.remove(Notif.NO_CROPS);
            workTile.seedStorageSlot = betterSeed;
            workTile.doDestroy = true;
            workTile.doHarvesting = false;
            workTile.doCrops = true;
            workTile.doPlanting = true;
          } else {
            workTile.farm.notifications.add(Notif.NO_CROPS);
          }
        }
      }
    }
  }

  private static int getCropsSticks(WorkTile workTile) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
    for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
      final ItemStack stack = workTile.farm.getStackInSlot(slot);
      if (stack != null && stack.getItem() != null) {
        return slot;
      }
    }
    return -1;
  }

  public static int hasBetterSeed(WorkTile workTile, ItemStack seed, int seedQuality) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
    for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
      final ItemStack stack = workTile.farm.getStackInSlot(slot);
      if (stack != null && SeedAnalyzerModule.isSameSeed(seed, stack)) {
        ISeedStats seedStats = workTile.agricraft.getSeedStats(stack);
        if (seedStats != null && seedStats.getGain() + seedStats.getGrowth() + seedStats.getStrength() > seedQuality) {
          return slot;
        }
      }
    }
    return -1;
  }

  @Override
  public int getPriority() {
    return 30;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof ReplaceBetterModule);
  }

}
