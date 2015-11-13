package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecuteDestroyModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doDestroy) {
      if (workTile.farm.canUsePower(100)) {
        if (damageHoe(workTile)) { // TODO: cfg
          workTile.farm.usePower(100); // TODO cfg
          List<ItemStack> result = null;
          final World world = workTile.farm.getWorldObj();
          result = workTile.agricraft.destroy(world, workTile.bc.x, workTile.bc.y, workTile.bc.z);
          spawnParticles(workTile);
          depositItems(workTile, result);
          workTile.doneSomething = true;
        }
      } else {
        workTile.farm.notifications.add(Notif.NO_POWER);
      }
    }
  }

  }
