package info.loenwind.enderioaddons.machine.waterworks;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerWaterworks extends AbstractMachineContainer<TileWaterworks> {

  public ContainerWaterworks(InventoryPlayer playerInv, TileWaterworks te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
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
