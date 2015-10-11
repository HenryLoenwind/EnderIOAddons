package info.loenwind.enderioaddons.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class StdSlot extends Slot {

  public StdSlot(IInventory inventory, int slotIndex, int x, int y) {
    super(inventory, slotIndex, x, y);
  }

  @Override
  public boolean isItemValid(ItemStack itemStack) {
    return inventory.isItemValidForSlot(getSlotIndex(), itemStack);
  }
}