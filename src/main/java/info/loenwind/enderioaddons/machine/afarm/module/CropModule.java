package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

public class CropModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if ((workTile.allowPlanting || workTile.allowCrossCrops) && !workTile.isCrops) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null && stack.getItem() != null
            && workTile.agricraft.canPlaceCrops(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z, stack)) {
          workTile.doCrops = true;
          workTile.cropsSlot = slot;
          workTile.farm.notifications.remove(Notif.NO_CROPS);
          return;
        }
      }
      for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null && stack.getItem() != null
            && !workTile.agricraft.canPlaceCrops(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z, stack)) {
          workTile.doTill = true; // TODO: This is guesswork
          workTile.farm.notifications.remove(Notif.NO_CROPS);
          return;
        }
      }
      for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
        if (workTile.farm.getStackInSlot(slot) != null) {
          return;
        }
      }
      workTile.farm.notifications.add(Notif.NO_CROPS);
    }
  }

  @Override
  public int getPriority() {
    return 51;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof CropModule);
  }

}
