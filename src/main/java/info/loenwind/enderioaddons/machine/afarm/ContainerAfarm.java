package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import info.loenwind.enderioaddons.gui.IHidableSlotsContainer;
import info.loenwind.enderioaddons.gui.StdOutputSlot;
import info.loenwind.enderioaddons.gui.StdSlot;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostSlot;

public class ContainerAfarm extends AbstractMachineContainerA<TileAfarm> implements IHidableSlotsContainer {

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
  private List<StdSlot> tab2slots;
  private List<StdSlot> tab3slots;
  private List<AfarmGhostSlot> tab1ghostSlotsEven;
  private List<AfarmGhostSlot> tab1ghostSlotsOdd;

  public ContainerAfarm(InventoryPlayer playerInv, @Nonnull TileAfarm te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    tab0slots = new ArrayList<>();
    tab1slots = new ArrayList<>();
    tab2slots = new ArrayList<>();
    tab3slots = new ArrayList<>();
    tab1ghostSlotsEven = new ArrayList<>();
    tab1ghostSlotsOdd = new ArrayList<>();

    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) getInv().getSlotDefinition();

    int x = 1;
    int y = ROW1;
    for (int slot = slotDef.getMinSlot(SLOT.CONTROL); slot <= slotDef
        .getMaxSlot(SLOT.CONTROL); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y) {
        @Override
        public int getSlotStackLimit() {
          return 1;
        }
      };
      addSlotToContainer(theSlot);
      tab0slots.add(theSlot);
    }

    x = 1;
    y = ROW25;
    for (int slot = slotDef.getMinSlot(SLOT.CONTROL_STORAGE); slot <= slotDef.getMaxSlot(SLOT.CONTROL_STORAGE); slot++) {
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
    for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef
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
    for (int slot = slotDef.getMinSlot(SLOT.OUTPUT); slot <= slotDef
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
    for (int slot = slotDef.getMinSlot(SLOT.TOOL); slot <= slotDef
        .getMaxSlot(SLOT.TOOL); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

    x = 4;
    y = ROW1;
    for (int slot = slotDef.getMinSlot(SLOT.FERTILIZER); slot <= slotDef.getMaxSlot(SLOT.FERTILIZER); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

    x = 1;
    y = ROW25;
    for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) getInv().getSlotDefinition();
    int x = COL + D;
    int y = ROW1;
    tab1ghostSlotsEven.clear();
    tab1ghostSlotsOdd.clear();
    boolean even = true;
    for (int slot = slotDef.getMinSlot(SLOT.SEED_GHOST); slot <= slotDef.getMaxSlot(SLOT.SEED_GHOST); slot++) {
      final AfarmGhostSlot theSlot = new AfarmGhostSlot(slot, x, y);
      x += D + D / 2;
      ghostSlots.add(theSlot);
      if (even) {
        tab1ghostSlotsEven.add(theSlot);
      } else {
        tab1ghostSlotsOdd.add(theSlot);
      }
      even = !even;
    }
  }

  private class AfarmGhostSlot extends GhostSlot {

    public AfarmGhostSlot(int slotIndex, int x, int y) {
      this.slot = slotIndex;
      this.x = x;
      this.y = y;
      this.te = getInv();
    }

    @Override
    public ItemStack getStack() {
      return getInv().getStackInSlot(slot);
    }

  }

  public void setTabVisibility(int tab, boolean twoGhosts) {
    for (StdSlot stdSlot : tab0slots) {
      stdSlot.enable(tab == 0);
    }
    for (StdSlot stdSlot : tab1slots) {
      stdSlot.enable(tab == 1);
    }
    for (StdSlot stdSlot : tab2slots) {
      stdSlot.enable(tab == 2);
    }
    for (StdSlot stdSlot : tab3slots) {
      stdSlot.enable(tab == 3);
    }
    for (AfarmGhostSlot slot : tab1ghostSlotsEven) {
      slot.visible = tab == 1;
    }
    for (AfarmGhostSlot slot : tab1ghostSlotsOdd) {
      slot.visible = (tab == 1) && !twoGhosts;
    }
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
