package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

public class CrossCropModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    // TODO: remove !isCrossCrops() when 1.4.5 is live
    if (workTile.allowCrossCrops && workTile.agricraft.isEmpty(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z)
        && !workTile.agricraft.isCrossCrops(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z)) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null) {
          workTile.cropsSlot = slot;
          workTile.doCrossCrops = true;
          return;
        }
      }
    }
  }

  @Override
  public int getPriority() {
    return 50;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof CrossCropModule);
  }

}
