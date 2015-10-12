package info.loenwind.enderioaddons.machine.voidtank;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import info.loenwind.enderioaddons.gui.StdSlot;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerVoidTank extends AbstractMachineContainerA<TileVoidTank> {

  public ContainerVoidTank(InventoryPlayer playerInv, TileVoidTank te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new StdSlot(getInv(), 0, 44, 21));
    addSlotToContainer(new StdSlot(getInv(), 1, 44, 52));
  }

}
