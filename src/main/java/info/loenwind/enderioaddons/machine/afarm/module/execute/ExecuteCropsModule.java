package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecuteCropsModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doCrops && workTile.farm.canUsePower(100)) { // TODO: cfg
      workTile.farm.usePower(100); // TODO cfg
      final World world = workTile.farm.getWorldObj();
      final ItemStack stack = workTile.farm.getStackInSlot(workTile.cropsSlot);
      workTile.agricraft.placeCrops(world, workTile.bc.x, workTile.bc.y, workTile.bc.z, stack);
      if (stack.stackSize <= 0) {
        workTile.farm.setInventorySlotContents(workTile.cropsSlot, null);
      }
      spawnParticles(workTile);
      workTile.farm.markDirty();
      workTile.doneSomething = true;
    }
  }

  @Override
  public int getPriority() {
    return super.getPriority() + 1;
  }

}
