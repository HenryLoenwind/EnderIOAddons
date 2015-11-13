package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

public class ExecuteTillModule extends ExecuteModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.doTill) {
      if (workTile.farm.canUsePower(100)) { // TODO: cfg
        int hoeSlot = workTile.farm.getHoeSlot();
        if (hoeSlot != -1) {
          final ItemStack stack = workTile.farm.getStackInSlot(hoeSlot);
          if (tillBlock(workTile, stack)) {
            workTile.farm.usePower(100); // TODO cfg
            workTile.farm.markDirty();
            spawnParticles(workTile);
            workTile.doneSomething = true;
          }
          if (stack.stackSize <= 0) {
            workTile.farm.setInventorySlotContents(hoeSlot, null);
            workTile.farm.markDirty();
          }
        }
      } else {
        workTile.farm.notifications.add(Notif.NO_POWER);
      }
    }
  }

  public static boolean tillBlock(WorkTile workTile, ItemStack tool) {
    BlockCoord dirtLoc = workTile.bc.getLocation(ForgeDirection.DOWN);
    boolean canDamage = canDamage(tool);
    int origDamage = tool.getItemDamage();
    final Block origBlock = dirtLoc.getBlock(workTile.farm.getWorldObj());
    final int origMeta = workTile.farm.getWorldObj().getBlockMetadata(dirtLoc.x, dirtLoc.y, dirtLoc.z);
    workTile.farmerJoe.inventory.mainInventory[0] = tool;
    workTile.farmerJoe.inventory.currentItem = 0;
    tool.getItem().onItemUse(tool, workTile.farmerJoe, workTile.farm.getWorldObj(), dirtLoc.x, dirtLoc.y, dirtLoc.z, 1, 0.5f, 0.5f, 0.5f);
    workTile.farmerJoe.inventory.mainInventory[0] = null;
    final Block block = dirtLoc.getBlock(workTile.farm.getWorldObj());
    final int meta = workTile.farm.getWorldObj().getBlockMetadata(dirtLoc.x, dirtLoc.y, dirtLoc.z);
    if (origBlock != block || origMeta != meta) {
      if (origDamage == tool.getItemDamage() && canDamage) {
        tool.damageItem(1, workTile.farmerJoe);
      }
      return true;
    } else if (origDamage != tool.getItemDamage()) {
      tool.setItemDamage(origDamage);
    }
    return false;
  }

}
