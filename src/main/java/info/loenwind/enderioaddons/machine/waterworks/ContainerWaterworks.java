package info.loenwind.enderioaddons.machine.waterworks;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerWaterworks extends AbstractMachineContainer<TileWaterworks> {

  private static final int D = 18;
  private static final int ROW = 9 - D;
  private static final int COL = 98 - D;

  public ContainerWaterworks(InventoryPlayer playerInv, TileWaterworks te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {

    int i = 0;
    for (int r = 1; r <= 2; r++) {
      for (int c = 1; c <= 3; c++) {
        addSlotToContainer(new OutputSlot(getInv(), getInv().outputSlotNo(i++), COL + c * D, ROW + r * D));
      }
    }
    for (int r = 3; r <= 4; r++) {
      for (int c = 1; c <= 4; c++) {
        addSlotToContainer(new OutputSlot(getInv(), getInv().outputSlotNo(i++), COL + c * D, ROW + r * D));
      }
    }
  }

  private class OutputSlot extends Slot {

    public OutputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
      return false;
    }
  }

}
