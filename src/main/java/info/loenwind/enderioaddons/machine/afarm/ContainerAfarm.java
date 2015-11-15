package info.loenwind.enderioaddons.machine.afarm;

import static info.loenwind.enderioaddons.config.Config.ghostItemsEnabled;
import info.loenwind.enderioaddons.gui.IHidableSlotsContainer;
import info.loenwind.enderioaddons.gui.StdOutputSlot;
import info.loenwind.enderioaddons.gui.StdSlot;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerAfarm extends AbstractMachineContainer<TileAfarm> implements IHidableSlotsContainer {

  static private final Random rand = new Random();

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
  private List<GhostSlot> tab0ghostSlots;
  private List<GhostSlot> tab1ghostSlots;
  private List<GhostSlot> tab1ghostSlotsOdd;
  private List<GhostSlot> tab2ghostSlots;
  private List<GhostSlot> tab3ghostSlots;

  public ContainerAfarm(InventoryPlayer playerInv, @Nonnull TileAfarm te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    tab0slots = new ArrayList<>();
    tab1slots = new ArrayList<>();
    tab2slots = new ArrayList<>();
    tab3slots = new ArrayList<>();
    tab0ghostSlots = new ArrayList<>();
    tab1ghostSlots = new ArrayList<>();
    tab2ghostSlots = new ArrayList<>();
    tab3ghostSlots = new ArrayList<>();
    tab1ghostSlotsOdd = new ArrayList<>();

    List<ItemStack> modules = getInv().getModules();
    List<ItemStack> fertilizers = getInv().getFertilizers();
    List<ItemStack> cropsticks = getInv().getCropsticks();
    List<ItemStack> tools = getInv().getTools();
    List<ItemStack> produce = getInv().getProduce();
    List<ItemStack> seeds = getInv().getSeeds();

    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) getInv().getSlotDefinition();

    int x = 1;
    int y = ROW1;
    for (int slot = slotDef.getMinSlot(SLOT.CONTROL); slot <= slotDef.getMaxSlot(SLOT.CONTROL); slot++) {
      tab0ghostSlots.add(new GhostBackgroundItemSlot(modules.get(rand.nextInt(modules.size())), COL + x * D, y));
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
      tab0ghostSlots.add(new GhostBackgroundItemSlot(modules.get(rand.nextInt(modules.size())), COL + x * D, y));
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
    for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
      tab1ghostSlots.add(new GhostBackgroundItemSlot(seeds.get(rand.nextInt(seeds.size())), COL + x * D, y));
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
    for (int slot = slotDef.getMinSlot(SLOT.OUTPUT); slot <= slotDef.getMaxSlot(SLOT.OUTPUT); slot++) {
      tab2ghostSlots.add(new GhostBackgroundItemSlot(produce.get(rand.nextInt(produce.size())), COL + x * D, y));
      final StdOutputSlot theSlot = new StdOutputSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab2slots.add(theSlot);
      if (x == 7) {
        x = 1;
        y += D;
      }
    }

    x = 1;
    y = ROW25;
    for (int slot = slotDef.getMinSlot(SLOT.TOOL); slot <= slotDef.getMaxSlot(SLOT.TOOL); slot++) {
      tab3ghostSlots.add(new GhostBackgroundItemSlot(tools.get(rand.nextInt(tools.size())), COL + x * D, y));
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

    x = 4;
    y = ROW25;
    for (int slot = slotDef.getMinSlot(SLOT.FERTILIZER); slot <= slotDef.getMaxSlot(SLOT.FERTILIZER); slot++) {
      tab3ghostSlots.add(new GhostBackgroundItemSlot(fertilizers.get(rand.nextInt(fertilizers.size())), COL + x * D, y));
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

    x = 1;
    y = ROW1;
    for (int slot = slotDef.getMinSlot(SLOT.CROPSTICK); slot <= slotDef.getMaxSlot(SLOT.CROPSTICK); slot++) {
      tab3ghostSlots.add(new GhostBackgroundItemSlot(cropsticks.get(rand.nextInt(cropsticks.size())), COL + x * D, y));
      final StdSlot theSlot = new StdSlot(getInv(), slot, COL + x++ * D, y);
      addSlotToContainer(theSlot);
      tab3slots.add(theSlot);
    }

  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) getInv().getSlotDefinition();
    int x = COL + D;
    int y = ROW1;
    boolean even = true;
    for (int slot = slotDef.getMinSlot(SLOT.SEED_GHOST); slot <= slotDef.getMaxSlot(SLOT.SEED_GHOST); slot++) {
      final AfarmGhostSlot theSlot = new AfarmGhostSlot(slot, x, y);
      x += D + D / 2;
      if (even) {
        tab1ghostSlots.add(theSlot);
      } else {
        tab1ghostSlotsOdd.add(theSlot);
      }
      even = !even;
    }
    if (ghostItemsEnabled.getBoolean()) {
      ghostSlots.addAll(tab0ghostSlots);
      ghostSlots.addAll(tab1ghostSlots);
      ghostSlots.addAll(tab1ghostSlotsOdd);
      ghostSlots.addAll(tab2ghostSlots);
      ghostSlots.addAll(tab3ghostSlots);
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
    for (GhostSlot slot : tab0ghostSlots) {
      slot.visible = tab == 0;
    }
    for (StdSlot stdSlot : tab1slots) {
      stdSlot.enable(tab == 1);
    }
    for (GhostSlot slot : tab1ghostSlots) {
      slot.visible = tab == 1;
    }
    for (GhostSlot slot : tab1ghostSlotsOdd) {
      slot.visible = (tab == 1) && !twoGhosts;
    }
    for (StdSlot stdSlot : tab2slots) {
      stdSlot.enable(tab == 2);
    }
    for (GhostSlot slot : tab2ghostSlots) {
      slot.visible = tab == 2;
    }
    for (StdSlot stdSlot : tab3slots) {
      stdSlot.enable(tab == 3);
    }
    for (GhostSlot slot : tab3ghostSlots) {
      slot.visible = tab == 3;
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
