package info.loenwind.enderioaddons.machine.drain;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDrain extends AbstractMachineContainerA<TileDrain> {

  public ContainerDrain(InventoryPlayer playerInv, TileDrain te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 116, 21) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 116, 52) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });

  }

}
