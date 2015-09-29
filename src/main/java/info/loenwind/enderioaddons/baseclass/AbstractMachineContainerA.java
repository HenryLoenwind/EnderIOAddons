package info.loenwind.enderioaddons.baseclass;

import info.loenwind.enderioaddons.common.NullHelper;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public abstract class AbstractMachineContainerA<T extends AbstractMachineEntity> extends AbstractMachineContainer<T> {

  public AbstractMachineContainerA(InventoryPlayer playerInv, T te) {
    super(playerInv, te);
  }

  @Override
  @Nonnull
  public T getInv() {
    return NullHelper.notnull(super.getInv(), "TileEntity went AWOL");
  }

}
