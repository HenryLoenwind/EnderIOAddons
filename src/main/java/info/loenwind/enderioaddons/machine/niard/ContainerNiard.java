package info.loenwind.enderioaddons.machine.niard;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import info.loenwind.enderioaddons.gui.StdSlot;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerNiard extends AbstractMachineContainerA<TileNiard> {

  public ContainerNiard(InventoryPlayer playerInv, TileNiard te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new StdSlot(getInv(), 0, 44, 21));
    addSlotToContainer(new StdSlot(getInv(), 1, 44, 52));
  }

}
