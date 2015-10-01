package info.loenwind.enderioaddons.machine.ihopper;

import static info.loenwind.enderioaddons.machine.ihopper.TileIHopper.SLOTS;
import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.network.PacketHandler;

public class ContainerIHopper extends AbstractMachineContainerA<TileIHopper> {

  static final int D = 18;
  static final int ROW1 = 9;
  static final int ROW2 = ROW1 + D + D / 2;
  static final int ROW3 = ROW2 + D + D / 2;
  static final int COL = 44 - D;

  public ContainerIHopper(InventoryPlayer playerInv, @Nonnull TileIHopper te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    for (int slot = 1; slot <= SLOTS; slot++) {
      addSlotToContainer(new InputSlot(getInv(), getInv().inputSlotNo(slot), COL + slot * D, ROW1));
    }
    for (int slot = 1; slot <= SLOTS; slot++) {
      addSlotToContainer(new OutputSlot(getInv(), getInv().outputSlotNo(slot), COL + slot * D, ROW3));
    }

  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    for (int slot = 1; slot <= SLOTS; slot++) {
      ghostSlots.add(new IHopperGhostSlot(getInv().ghostSlotNo(slot), COL + slot * D, ROW2));
    }
  }

  private class InputSlot extends Slot {

    public InputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
      return getInv().isItemValidForSlot(getSlotIndex(), itemStack);
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

  private class IHopperGhostSlot extends GhostSlot {
    private final int slotIndex;

    public IHopperGhostSlot(int slotIndex, int x, int y) {
      this.slotIndex = slotIndex;
      this.x = x;
      this.y = y;
    }

    @Override
    public ItemStack getStack() {
      return getInv().getStackInSlot(slotIndex);
    }

    @Override
    public void putStack(ItemStack newstack) {
      PacketHandler.INSTANCE.sendToServer(PacketIHopper.setSlot(getInv(), slotIndex, newstack));
    }
  }

}
