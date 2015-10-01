package info.loenwind.enderioaddons.gui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketAdvancedRedstoneMode implements IMessage, IMessageHandler<PacketAdvancedRedstoneMode, IMessage> {

  private int x;
  private int y;
  private int z;
  private AdvancedRedstoneMode mode;

  public PacketAdvancedRedstoneMode() {
  }

  public PacketAdvancedRedstoneMode(IAdvancedRedstoneModeControlable cont, int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    mode = cont.getAdvancedRedstoneControlMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort((short) mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    short ordinal = buf.readShort();
    mode = AdvancedRedstoneMode.values()[ordinal];
  }

  @Override
  public IMessage onMessage(PacketAdvancedRedstoneMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if (te instanceof IAdvancedRedstoneModeControlable) {
      IAdvancedRedstoneModeControlable me = (IAdvancedRedstoneModeControlable) te;
      me.setAdvancedRedstoneControlMode(message.mode);
    }
    return null;
  }

}
