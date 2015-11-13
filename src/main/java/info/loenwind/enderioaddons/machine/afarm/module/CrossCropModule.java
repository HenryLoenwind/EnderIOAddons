package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

public class CrossCropModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.allowCrossCrops && workTile.isEmpty && !workTile.isCrossCrops && !workTile.isWeeds) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null) {
          workTile.cropsSlot = slot;
          workTile.doCrossCrops = true;
          workTile.farm.notifications.remove(Notif.NO_CROPS);
          return;
        }
      }
      workTile.farm.notifications.add(Notif.NO_CROPS);
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
