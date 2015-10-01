package info.loenwind.enderioaddons.machine.ihopper;

import crazypants.enderio.machine.SlotDefinition;

public class GhostlySlotDefinition extends SlotDefinition {

  public final int minGhostSlot;
  public final int maxGhostSlot;

  public GhostlySlotDefinition(int numInputs, int numOutputs, int numUpgradeSlots, int numGostSlots) {
    super(numInputs, numOutputs, numUpgradeSlots);
    this.minGhostSlot = numGostSlots > 0 ? numInputs + numOutputs + numUpgradeSlots : -1;
    this.maxGhostSlot = minGhostSlot + (numGostSlots - 1);
  }

  public GhostlySlotDefinition(int minInputSlot, int maxInputSlot, int minOutputSlot, int maxOutputSlot, int minUpgradeSlot, int maxUpgradeSlot,
      int minGhostSlot, int maxGhostSlot) {
    super(minInputSlot, maxInputSlot, minOutputSlot, maxOutputSlot, minUpgradeSlot, maxUpgradeSlot);
    this.minGhostSlot = minGhostSlot;
    this.maxGhostSlot = maxGhostSlot;
  }

  @Override
  public int getNumSlots() {
    return max(getMaxInputSlot(), getMaxOutputSlot(), getMaxUpgradeSlot(), getMaxGhostSlot()) + 1;
  }

  public int getMinGhostSlot() {
    return minGhostSlot;
  }

  public int getMaxGhostSlot() {
    return maxGhostSlot;
  }

  public boolean isGhostSlot(int slot) {
    return slot >= minGhostSlot && slot <= maxGhostSlot;
  }

  private static int max(int... a) {
    int x = 0;
    for (int i : a) {
      if (i > x) {
        x = i;
      }
    }
    return x;
  }

}
