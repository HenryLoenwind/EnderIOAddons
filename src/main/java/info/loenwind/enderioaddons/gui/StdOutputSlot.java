package info.loenwind.enderioaddons.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class StdOutputSlot extends StdSlot {

  public StdOutputSlot(IInventory inventory, int slotIndex, int x, int y) {
    super(inventory, slotIndex, x, y);
  }

  @Override
  public boolean isItemValid(ItemStack itemStack) {
    return false;
  }

}