package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;

public class ReplaceBetterModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (!workTile.allowHarvesting && !workTile.doHarvesting && workTile.seedSlot != -1) {
      ISeedStats stats = workTile.agricraft.getStats(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z);
      if (stats != null && stats.getGain() != -1
          && stats.getGain() + stats.getGrowth() + stats.getStrength() < stats.getMaxGain() + stats.getMaxGrowth() + stats.getMaxStrength()) {
        ItemStack seed = workTile.agricraft.getPlantedSeed(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z);
        if (seed != null) {
          int seedQuality = stats.getGain() + stats.getGrowth() + stats.getStrength();
          workTile.seedStorageSlot = hasBetterSeed(workTile, seed, seedQuality);
          workTile.doHarvesting = workTile.seedStorageSlot != -1;
        }
      }
    }
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
