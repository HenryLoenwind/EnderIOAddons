package info.loenwind.enderioaddons.machine.afarm.module.execute;

import static info.loenwind.enderioaddons.config.Config.farmRFperRaking;
import static info.loenwind.enderioaddons.config.Config.farmRFperRakingIndustrial;
import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExecuteWeedModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doWeed) {
      if (workTile.farm.canUsePower(farmRFperRaking.getInt())) {
        int rakeSlot = workTile.farm.getRakeSlot();
        if (rakeSlot != -1) {
          final ItemStack stack = workTile.farm.getStackInSlot(rakeSlot);
          boolean isIndustrialRake = stack.getItem() == ItemMachinePart.itemMachinePart && stack.getItemDamage() == MachinePart.IRAKE.ordinal();
          final World world = workTile.farm.getWorldObj();
          // TODO switch back after agricraft 1.4.5
          //        : workTile.agricraft.removeWeeds(world, workTile.bc.x, workTile.bc.y, workTile.bc.z, stack)) {
          if (isIndustrialRake ? workTile.agricraft.removeWeeds(world, workTile.bc.x, workTile.bc.y, workTile.bc.z, false) : workTile.agricraft.removeWeeds(
              world, workTile.bc.x, workTile.bc.y, workTile.bc.z, false)) {
            workTile.farm.usePower(isIndustrialRake ? farmRFperRakingIndustrial.getInt() : farmRFperRaking.getInt());
            spawnParticles(workTile);
            workTile.doneSomething = true;
          }
          if (stack.stackSize <= 0) {
            workTile.farm.setInventorySlotContents(workTile.cropsSlot, null);
          }
          workTile.farm.markDirty();
        }
      } else {
        workTile.farm.notifications.add(Notif.NO_POWER);
      }
    } else if (workTile.isWeeds) {
      workTile.farm.notifications.add(Notif.WEEDS);
    }
  }

}
