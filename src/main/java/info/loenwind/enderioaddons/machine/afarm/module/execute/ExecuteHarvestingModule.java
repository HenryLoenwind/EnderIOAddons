package info.loenwind.enderioaddons.machine.afarm.module.execute;

import static info.loenwind.enderioaddons.config.Config.farmRFperHarvest;
import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecuteHarvestingModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doHarvesting) {
      if (workTile.farm.canUsePower(farmRFperHarvest.getInt())) {
        if (damageHoe(workTile)) {
          workTile.farm.usePower(farmRFperHarvest.getInt());
          List<ItemStack> result = null;
          final World world = workTile.farm.getWorldObj();
          result = workTile.agricraft.harvest(world, workTile.bc.x, workTile.bc.y, workTile.bc.z);
          spawnParticles(workTile);
          depositItems(workTile, result);
        }
      } else {
        workTile.farm.notifications.add(Notif.NO_POWER);
      }
    }
  }

}
