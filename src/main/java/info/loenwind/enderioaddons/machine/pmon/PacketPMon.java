package info.loenwind.enderioaddons.machine.pmon;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;

public class PacketPMon extends MessageTileEntity<TilePMon> {

  private int no, collectCount, pos;
  private byte[] data;

  public PacketPMon() {
  }

  private PacketPMon(TilePMon tile) {
    super(tile);
  }

  public static IMessage requestUpdate(TilePMon te, int no) {
    PacketPMon msg = new PacketPMon(te);
    msg.no = no;
    msg.collectCount = -1;
    msg.pos = -1;
    msg.data = null;
    return msg;
  }

  public static IMessage sendUpdate(TilePMon te, int no) {
    PacketPMon msg = new PacketPMon(te);
    msg.no = no;
    msg.collectCount = te.stats[no].getCollectCount();
    msg.pos = te.stats[no].getPos();
    msg.data = te.stats[no].getData();
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    no = buf.readShort();
    collectCount = buf.readShort();
    if (collectCount >= 0) {
      pos = buf.readShort();
      data = new byte[StatArray.BYTES * 2];
      buf.readBytes(data);
    } else {
      pos = -1;
      data = null;
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(no);
    buf.writeShort(collectCount);
    if (collectCount >= 0) {
      buf.writeShort(pos);
      buf.writeBytes(data);
    }
  }

  public static class ServerHandler implements IMessageHandler<PacketPMon, IMessage> {

    @Override
    public IMessage onMessage(PacketPMon msg, MessageContext ctx) {
      TilePMon te = msg.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
      if (te != null && msg.no >= 0 && msg.no < te.stats.length && ctx.getServerHandler() != null && ctx.getServerHandler().playerEntity != null) {
        PacketHandler.sendTo(sendUpdate(te, msg.no), ctx.getServerHandler().playerEntity);
      }
      return null;
    }
  }

  public static class ClientHandler implements IMessageHandler<PacketPMon, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketPMon msg, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      if (player != null) {
        TilePMon te = msg.getTileEntity(player.worldObj);
        if (te != null && msg.no >= 0 && msg.no < te.stats.length) {
          te.stats[msg.no].setCollectCount(msg.collectCount);
          te.stats[msg.no].setPos(msg.pos);
          te.stats[msg.no].setData(msg.data);
          if (msg.no == te.stats.length - 1 && te.dynaTextureProvider != null) {
            ((DynaTextureProvider) te.dynaTextureProvider).updateTexture();
          }
        }
      }
      return null;
    }
  }

}
