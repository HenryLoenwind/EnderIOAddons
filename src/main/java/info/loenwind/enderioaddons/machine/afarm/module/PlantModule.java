package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.SeedRequirementStatus;

public class PlantModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.allowPlanting && !workTile.doPlanting && isEmpty(workTile)) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      final ItemStack template = workTile.seedSlot != -1 ? workTile.farm.getStackInSlot(workTile.seedSlot) : null;
      for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
        final ItemStack stack = workTile.farm.getStackInSlot(slot);
        if (stack != null && stack.getItem() != null) {
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
    }
  }

  public static boolean isEmpty(WorkTile workTile) {
    return workTile.agricraft.isEmpty(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z);
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
