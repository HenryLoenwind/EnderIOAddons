package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecutePlantingModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doPlanting) {
      if (workTile.farm.canUsePower(100)) {
        if (damageHoe(workTile)) { // TODO: cfg
          workTile.farm.usePower(100); // TODO cfg
          final World world = workTile.farm.getWorldObj();
          final ItemStack stack = workTile.farm.getStackInSlot(workTile.seedStorageSlot);
          workTile.agricraft.applySeeds(world, workTile.bc.x, workTile.bc.y, workTile.bc.z, stack);
          if (stack.stackSize <= 0) {
            workTile.farm.setInventorySlotContents(workTile.seedStorageSlot, null);
          }
          spawnParticles(workTile);
          workTile.farm.markDirty();
        }
      } else {
        workTile.farm.notifications.add(Notif.NO_POWER);
      }
    }
  }

  @Override
  public int getPriority() {
    return super.getPriority() + 3;
  }

}
