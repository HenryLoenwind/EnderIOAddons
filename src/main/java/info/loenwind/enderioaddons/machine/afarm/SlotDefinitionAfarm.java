package info.loenwind.enderioaddons.machine.afarm;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import crazypants.enderio.machine.SlotDefinition;

public class SlotDefinitionAfarm extends SlotDefinition {

  public enum SLOT {
    CONTROL, CONTROL_STORAGE, SEED, OUTPUT, TOOL, FERTILIZER, CROPSTICK, UPGRADE;
  }

  private final List<SLOT> slottypes = new ArrayList<>();
  private final Map<SLOT, Integer> minslots = new EnumMap<>(SLOT.class);
  private final Map<SLOT, Integer> maxslots = new EnumMap<>(SLOT.class);

  public SlotDefinitionAfarm(int num_control_slots, int num_control_storage_slots, int num_seed_storage_slots, int num_output_slots, int num_tool_slots,
      int num_fertilizer_slots, int num_cropstick_slots, int num_upgrade_slots) {
    super(num_control_storage_slots + num_seed_storage_slots + num_tool_slots + num_fertilizer_slots + num_cropstick_slots, num_output_slots, num_upgrade_slots);
    addSlot(SLOT.CONTROL_STORAGE, num_control_storage_slots);
    addSlot(SLOT.SEED, num_seed_storage_slots);
    addSlot(SLOT.TOOL, num_tool_slots);
    addSlot(SLOT.FERTILIZER, num_fertilizer_slots);
    addSlot(SLOT.CROPSTICK, num_cropstick_slots);
    addSlot(SLOT.OUTPUT, num_output_slots);
    addSlot(SLOT.UPGRADE, num_upgrade_slots);
    addSlot(SLOT.CONTROL, num_control_slots);
  }

  private void addSlot(SLOT slottype, int num_slots) {
    minslots.put(slottype, slottypes.size());
    for (int i = 1; i <= num_slots; i++) {
      slottypes.add(slottype);
    }
    maxslots.put(slottype, slottypes.size() - 1);
  }

  @Override
  public int getNumSlots() {
    return slottypes.size();
  }

  public boolean isSlot(SLOT slottype, int slotno) {
    return slottypes.get(slotno) == slottype;
  }

  public SLOT getSlotType(int slotno) {
    return slottypes.get(slotno);
  }

  public int getMinSlot(SLOT slottype) {
    return minslots.get(slottype);
  }

  public int getMaxSlot(SLOT slottype) {
    return maxslots.get(slottype);
  }

}
