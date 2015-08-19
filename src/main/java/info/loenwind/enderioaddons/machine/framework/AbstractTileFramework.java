package info.loenwind.enderioaddons.machine.framework;

import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;

public abstract class AbstractTileFramework extends AbstractPowerConsumerEntity {

  public AbstractTileFramework(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    if (isSideDisabled(var1)) {
      return new int[0];
    }

    int[] res = new int[inventory.length - slotDefinition.getNumUpgradeSlots()];
    int index = 0;
    for (int i = 0; i < inventory.length; i++) {
      if (!slotDefinition.isUpgradeSlot(i)) {
        res[index] = i;
        index++;
      }
    }
    return res;
  }

}