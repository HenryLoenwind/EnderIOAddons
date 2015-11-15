package info.loenwind.enderioaddons.machine.pmon;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

class ContainerPMon extends AbstractMachineContainer<TilePMon> {

  public ContainerPMon(InventoryPlayer playerInv, TilePMon te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
