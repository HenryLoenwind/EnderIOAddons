package info.loenwind.enderioaddons.machine.ihopper;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerIHopper extends AbstractMachineContainerA<TileIHopper> {

  private static final int D = 18;
  private static final int ROW1 = 9;
  private static final int ROW2 = ROW1 + D + D / 2;
  private static final int ROW3 = ROW2 + D + D / 2;
  private static final int COL = 44 - D;

  public ContainerIHopper(InventoryPlayer playerInv, @Nonnull TileIHopper te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {

    for (int r = 1; r <= 6; r++) {
      addSlotToContainer(new Slot(getInv(), getInv().inputSlotNo(r), COL + r * D, ROW1));
    }
    // TODO Ghost slots here
    for (int r = 1; r <= 6; r++) {
      addSlotToContainer(new OutputSlot(getInv(), getInv().outputSlotNo(r), COL + r * D, ROW3));
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
