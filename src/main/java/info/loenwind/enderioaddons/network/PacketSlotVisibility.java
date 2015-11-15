package info.loenwind.enderioaddons.network;

import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.gui.IHidableSlotsContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSlotVisibility implements IMessage, IMessageHandler<PacketSlotVisibility, IMessage> {

  private int slotno = 0;
  private boolean hide = true;

  public PacketSlotVisibility() {
  }

  public PacketSlotVisibility(int slotNo, boolean hide) {
    this.slotno = slotNo;
    this.hide = hide;
    if (slotno > Byte.MAX_VALUE) {
      throw new RuntimeException("More than " + Byte.MAX_VALUE + " slots in one GUI? Update the packet!");
    }
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    slotno = bb.readByte();
    hide = bb.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf bb) {
    bb.writeByte(slotno);
    bb.writeBoolean(hide);
  }

  @Override
  public IMessage onMessage(PacketSlotVisibility message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    if (player.openContainer instanceof IHidableSlotsContainer) {
      ((IHidableSlotsContainer) player.openContainer).hideSlot(message.slotno, message.hide);
    } else {
      Log.warn("Recieved PacketSlotVisibility but current GUI isn't compatible");
    }
    return null;
  }
}
