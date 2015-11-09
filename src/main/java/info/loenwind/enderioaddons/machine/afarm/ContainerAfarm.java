package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import info.loenwind.enderioaddons.gui.StdOutputSlot;
import info.loenwind.enderioaddons.gui.StdSlot;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostSlot;

public class ContainerAfarm extends AbstractMachineContainerA<TileAfarm> {

  static final int D = 18;
  static final int ROW1 = 17;
  static final int ROW15 = ROW1 + D / 2;
  static final int ROW2 = ROW1 + D;
  static final int ROW25 = ROW1 + D + D / 2 + 1;
  static final int ROW3 = ROW2 + D;
  static final int ROW35 = ROW2 + D + D / 2 + 1;
  static final int COL = 35 - D;

  private List<StdSlot> tab0slots;
  private List<StdSlot> tab1slots;
  private List<StdOutputSlot> tab2slots;
  private List<StdSlot> tab3slots;

  public ContainerAfarm(InventoryPlayer playerInv, @Nonnull TileAfarm te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    tab0slots = new ArrayList<>();
    tab1slots = new ArrayList<>();
    tab2slots = new ArrayList<>();
    tab3slots = new ArrayList<>();

    int x = 1;
    int y = ROW1;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.CONTROL); slot <= ((SlotDefinitionAfarm) getInv().getSlotDefinition())
        .getMaxSlot(SLOT.CONTROL); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab0slots.add(theSlot);
    }

    x = 1;
    y = ROW25;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.CONTROL_STORAGE); slot <= ((SlotDefinitionAfarm) getInv()
        .getSlotDefinition()).getMaxSlot(SLOT.CONTROL_STORAGE); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab0slots.add(theSlot);
      if (x == 7) {
        x = 1;
        y += D;
      }
    }

    x = 1;
    y = ROW25;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.SEED); slot <= ((SlotDefinitionAfarm) getInv().getSlotDefinition())
        .getMaxSlot(SLOT.SEED); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab1slots.add(theSlot);
      if (x == 7) {
        x = 1;
        y += D;
      }
    }

    x = 1;
    y = ROW1;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.OUTPUT); slot <= ((SlotDefinitionAfarm) getInv().getSlotDefinition())
        .getMaxSlot(SLOT.OUTPUT); slot++) {
      final StdOutputSlot theSlot = new StdOutputSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab2slots.add(theSlot);
      if (x == 7) {
        x = 1;
        y += D;
      }
    }

    x = 1;
    y = ROW1;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.TOOL); slot <= ((SlotDefinitionAfarm) getInv().getSlotDefinition())
        .getMaxSlot(SLOT.TOOL); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

    x = 4;
    y = ROW1;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.FERTILIZER); slot <= ((SlotDefinitionAfarm) getInv()
        .getSlotDefinition()).getMaxSlot(SLOT.FERTILIZER); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

    x = 1;
    y = ROW25;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.CROPSTICK); slot <= ((SlotDefinitionAfarm) getInv()
        .getSlotDefinition()).getMaxSlot(SLOT.CROPSTICK); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    int x = COL + D;
    int y = ROW1;
    for (int slot = ((SlotDefinitionAfarm) getInv().getSlotDefinition()).getMinSlot(SLOT.CONTROL); slot <= ((SlotDefinitionAfarm) getInv().getSlotDefinition())
        .getMaxSlot(SLOT.CONTROL); slot++) {
      final IHopperGhostSlot theSlot = new IHopperGhostSlot(slot, x, y);
      x += D + D / 2;
      ghostSlots.add(theSlot);
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
      // TODO      PacketHandler.INSTANCE.sendToServer(PacketIHopper.setGhostSlotContents(getInv(), slotIndex, newstack));
    }
  }

  public void setTabVisibility(int tab) {
    for (StdSlot stdSlot : tab0slots) {
      stdSlot.enable(tab == 0);
    }
    for (StdSlot stdSlot : tab1slots) {
      stdSlot.enable(tab == 1);
    }
    for (StdOutputSlot stdSlot : tab2slots) {
      stdSlot.enable(tab == 2);
    }
    for (StdSlot stdSlot : tab3slots) {
      stdSlot.enable(tab == 3);
    }
  }

}
