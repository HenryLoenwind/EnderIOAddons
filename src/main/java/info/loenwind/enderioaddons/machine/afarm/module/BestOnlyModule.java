package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;

public class BestOnlyModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
    for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
      final ItemStack stack = workTile.farm.getStackInSlot(slot);
      if (stack != null) {
        ISeedStats stats = workTile.agricraft.getSeedStats(stack);
        if (stats != null && stats.getGain() != -1) {
          int seedQuality = stats.getGain() + stats.getGrowth() + stats.getStrength();
          if (seedQuality < stats.getMaxGain() + stats.getMaxGrowth() + stats.getMaxStrength()
              && ReplaceBetterModule.hasBetterSeed(workTile, stack, seedQuality) != -1) {
            EjectSeedsModule.ejectSlotToOutput(workTile, slot);
          }
        }
      }
    }
  }

  @Override
  public int getPriority() {
    return 110;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof BestOnlyModule) && !(other instanceof EjectSeedsModule);
  }

}
