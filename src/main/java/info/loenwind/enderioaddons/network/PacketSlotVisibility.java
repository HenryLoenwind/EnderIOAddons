package info.loenwind.enderioaddons.network;

import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.gui.IHidableSlotsContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSlotVisibility implements IMessage, IMessageHandler<PacketSlotVisibility, IMessage> {

  private int mask = 0;
  private int hide = 0;

  public PacketSlotVisibility() {
  }

  public PacketSlotVisibility(int hide) {
    this.mask = 0b11111111;
    this.hide = hide;
  }

  public PacketSlotVisibility(int slotNo, boolean hide) {
    this.mask |= 1 << slotNo;
    if (hide) {
      this.hide |= 1 << slotNo;
    } else {
      this.hide &= ~(1 << slotNo);
    }
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    mask = bb.readUnsignedByte();
    hide = bb.readUnsignedByte();
  }

  @Override
  public void toBytes(ByteBuf bb) {
    bb.writeByte(mask);
    bb.writeByte(hide);
  }

  @Override
  public IMessage onMessage(PacketSlotVisibility message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    if (player.openContainer instanceof IHidableSlotsContainer) {
      IHidableSlotsContainer c = (IHidableSlotsContainer) player.openContainer;
      for (int i = 0; i < 8; i++) {
        if ((message.mask & (1 << i)) != 0) {
          c.hideSlot(i, (message.hide & (1 << i)) != 0);
        }
      }
    } else {
      Log.warn("Recieved PacketSlotVisibility but current GUI isn't compatible");
    }
    return null;
  }
}
