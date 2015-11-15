package info.loenwind.enderioaddons.machine.tcom;

import info.loenwind.enderioaddons.gui.IHidableSlotsContainer;
import info.loenwind.enderioaddons.gui.StdOutputSlot;
import info.loenwind.enderioaddons.gui.StdSlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerTcom extends AbstractMachineContainer<TileTcom> implements IHidableSlotsContainer {

  public ContainerTcom(InventoryPlayer playerInv, TileTcom te) {
    super(playerInv, te);
  }

  protected Slot in0, in1, out0, out1;

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(in0 = new StdSlot(getInv(), 0, 20, 15));
    addSlotToContainer(in1 = new StdSlot(getInv(), 1, 20, 15));
    addSlotToContainer(out0 = new StdOutputSlot(getInv(), 2, 20, 53));
    addSlotToContainer(out1 = new StdOutputSlot(getInv(), 3, 20, 53));
  }

  @Override
  public void hideSlot(int slotno, boolean hide) {
    for (Object slot : inventorySlots) {
      if (((Slot) slot).getSlotIndex() == slotno) {
        if (slot instanceof StdSlot) {
          ((StdSlot) slot).enable(!hide);
        }
      }
    }
  }

}
