package info.loenwind.enderioaddons.machine.pmon;

import info.loenwind.enderioaddons.baseclass.AbstractMachineContainerA;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPMon extends AbstractMachineContainerA<TilePMon> {

  public ContainerPMon(InventoryPlayer playerInv, TilePMon te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
