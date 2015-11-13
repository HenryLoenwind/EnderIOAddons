package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.SeedRequirementStatus;

public class PlantModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.allowPlanting && !workTile.doPlanting && workTile.isEmpty) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      final ItemStack template = workTile.seedSlot != -1 ? workTile.farm.getStackInSlot(workTile.seedSlot + slotDef.getMinSlot(SLOT.SEED_GHOST)) : null;
      boolean foundSeeds = false;
      for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null && stack.getItem() != null) {
          workTile.farm.notifications.remove(Notif.NO_SEEDS);
          foundSeeds = true;
          if (template != null && !SeedAnalyzerModule.isSameSeed(template, stack)) {
            continue;
          }
          SeedRequirementStatus status = workTile.agricraft.canApplySeeds(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z, stack);
          if (status == SeedRequirementStatus.NEEDS_TILLING) {
            workTile.doTill = true;
            return;
          } else if (status == SeedRequirementStatus.CAN_APPLY) {
            workTile.doPlanting = true;
            workTile.seedStorageSlot = slot;
            return;
          }
        }
      }
      if (!foundSeeds) {
        workTile.farm.notifications.add(Notif.NO_SEEDS);
      }
    }
  }

  @Override
  public int getPriority() {
    return 40;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof PlantModule);
  }

}
