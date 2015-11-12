package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecuteCrossCropsModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doCrossCrops && workTile.farm.canUsePower(100)) { // TODO: cfg
      workTile.farm.usePower(100); // TODO cfg
      final World world = workTile.farm.getWorldObj();
      final ItemStack stack = workTile.farm.getStackInSlot(workTile.cropsSlot);
      workTile.agricraft.placeCrossCrops(world, workTile.bc.x, workTile.bc.y, workTile.bc.z, stack);
      if (stack.stackSize <= 0) {
        workTile.farm.setInventorySlotContents(workTile.cropsSlot, null);
      }
      spawnParticles(workTile);
      workTile.farm.markDirty();
    }
  }

  @Override
  public int getPriority() {
    return super.getPriority() + 2;
  }

}
