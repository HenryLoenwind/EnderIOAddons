package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecuteWeedModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doWeed && workTile.farm.canUsePower(100)) { // TODO: cfg
      workTile.farm.usePower(100); // TODO cfg
      int rakeSlot = workTile.farm.getRakeSlot();
      if (rakeSlot != -1) {
        final ItemStack stack = workTile.farm.getStackInSlot(rakeSlot);
        final World world = workTile.farm.getWorldObj();
        workTile.agricraft.removeWeeds(world, workTile.bc.x, workTile.bc.y, workTile.bc.z, stack);
        if (stack.stackSize <= 0) {
          workTile.farm.setInventorySlotContents(workTile.cropsSlot, null);
        }
        spawnParticles(workTile);
        workTile.farm.markDirty();
        workTile.doneSomething = true;
      }
    }
  }

}
