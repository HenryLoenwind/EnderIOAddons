package info.loenwind.enderioaddons.machine.niard;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerNiard extends AbstractMachineContainerA<TileNiard> {

  public ContainerNiard(InventoryPlayer playerInv, TileNiard te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    //TODO add in slots instead of out slots
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
