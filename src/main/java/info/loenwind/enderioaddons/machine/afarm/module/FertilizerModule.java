package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

public class FertilizerModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (!workTile.doDestroy && !workTile.doHarvesting && !workTile.doPlanting && workTile.isCrops && !workTile.isEmpty && !workTile.isMature) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      boolean foundFertilizer = false;
      for (int slot = slotDef.getMinSlot(SLOT.FERTILIZER); slot <= slotDef.getMaxSlot(SLOT.FERTILIZER); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null) {
          workTile.farm.notifications.remove(Notif.NO_FERTILIZER);
          foundFertilizer = true;
          if (workTile.agricraft.isValidFertilizer(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z, stack)) {
            workTile.doFertilize = true;
            workTile.fertilizerSlot = slot;
            return;
          }
        }
      }
      if (!foundFertilizer) {
        workTile.farm.notifications.add(Notif.NO_FERTILIZER);
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
