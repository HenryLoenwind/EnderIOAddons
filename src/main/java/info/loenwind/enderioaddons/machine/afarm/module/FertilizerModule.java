package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

public class FertilizerModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (!workTile.doDestroy && !workTile.doHarvesting && !workTile.doPlanting) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      for (int slot = slotDef.getMinSlot(SLOT.FERTILIZER); slot <= slotDef.getMaxSlot(SLOT.FERTILIZER); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null) {
          if (workTile.agricraft.isCrops(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z)
              && !workTile.agricraft.isEmpty(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z)
              && !workTile.agricraft.isMature(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z)
              && workTile.agricraft.isValidFertilizer(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z, stack)) {
            workTile.doFertilize = true;
            workTile.fertilizerSlot = slot;
            return;
          }
        }
      }
    }
  }

  @Override
  public int getPriority() {
    return 70;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof FertilizerModule);
  }

}
