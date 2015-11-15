package info.loenwind.enderioaddons.machine.voidtank;

import info.loenwind.enderioaddons.gui.StdSlot;
import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerVoidTank extends AbstractMachineContainer<TileVoidTank> {

  public ContainerVoidTank(InventoryPlayer playerInv, TileVoidTank te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new StdSlot(getInv(), 0, 44, 21));
    addSlotToContainer(new StdSlot(getInv(), 1, 44, 52));
  }

}
