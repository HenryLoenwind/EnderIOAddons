package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.Notif;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.ItemUtil;

public class EjectSeedsModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
    for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
      ejectSlotToOutput(workTile, slot);
    }
  }

  public static void ejectSlotToOutput(WorkTile workTile, int slot) {
    ItemStack stack = workTile.farm.getStackInSlot(slot);
    stack = putIntoOutput(workTile, stack);
    if (stack != null && stack.stackSize == 0) {
      stack = null;
    }
    workTile.farm.setInventorySlotContents(slot, stack);
    workTile.farm.markDirty();
  }

  public static ItemStack putIntoOutput(WorkTile workTile, ItemStack stack) {
    int firstfree = -1;
    if (stack != null) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      for (int oslot = slotDef.getMinSlot(SLOT.OUTPUT); oslot <= slotDef.getMaxSlot(SLOT.OUTPUT); oslot++) {
        final ItemStack ostack = workTile.farm.getStackInSlot(oslot);
        if (ostack != null) {
          if (ItemUtil.areStackMergable(stack, ostack) && ostack.stackSize < ostack.getMaxStackSize()) {
            int free = ostack.getMaxStackSize() - ostack.stackSize;
            if (free >= stack.stackSize) {
              ostack.stackSize += stack.stackSize;
              stack.stackSize = 0;
              break;
            } else {
              ostack.stackSize = ostack.getMaxStackSize();
              stack.stackSize -= free;
            }
          }
        } else if (firstfree == -1) {
          firstfree = oslot;
        }
      }
      if (stack.stackSize > 0) {
        if (firstfree != -1) {
          workTile.farm.setInventorySlotContents(firstfree, stack);
          stack.stackSize = 0;
          workTile.farm.notifications.remove(Notif.FULL);
        } else {
          workTile.farm.notifications.add(Notif.FULL);
        }
      } else {
        workTile.farm.notifications.remove(Notif.FULL);
      }
    }
    return stack;
  }

  @Override
  public int getPriority() {
    return 110;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof EjectSeedsModule) && !(other instanceof SeedAnalyzerModule);
  }

}
