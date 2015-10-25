package info.loenwind.enderioaddons.network;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketNetworkUpdate extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketNetworkUpdate, IMessage> {

  private int[] data;

  public PacketNetworkUpdate() {
  }

  public PacketNetworkUpdate(@Nonnull TileEntity tile, int[] data) {
    super(tile);
    this.data = data;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(data.length);
    for (int i : data) {
      buf.writeInt(i);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    int len = buf.readByte();
    data = new int[len];
    for (int i = 0; i < len; i++) {
      data[i] = buf.readInt();
    }
  }

  @Override
  public IMessage onMessage(PacketNetworkUpdate message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity tile = message.getTileEntity(player.worldObj);
    if (tile instanceof INetworkUpdatable) {
      INetworkUpdatable te = (INetworkUpdatable) tile;
      for (int i = 0; i < message.data.length; i++) {
        te.networkUpdate(i, message.data[i]);
      }
    }
    return null;
  }
}