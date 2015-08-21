package info.loenwind.enderioaddons.machine.waterworks;

import java.awt.Point;

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
  public Point getPlayerInventoryOffset() {
    return new Point(37, 84);
  }

  @Override
  public Point getUpgradeOffset() {
    return new Point(12, 60);
  }

  private final int COL_COBBLE = 43;
  private final int COL_INPUT_1 = 43;
  private final int COL_OUTPUT_1 = 73;
  private final int COL_OFFSET = 54;

  private final int ROW_INPUT = 35;
  private final int ROW_COBBLE = 8;
  private final int ROW_OUTPUT_1 = 8;
  private final int ROW_OFFSET = 18;

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
