package info.loenwind.enderioaddons.machine.tcom;

import info.loenwind.enderioaddons.machine.tcom.engine.Mats;
import io.netty.buffer.ByteBuf;

import java.security.InvalidParameterException;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTcomAction extends MessageTileEntity<TileTcom> implements IMessageHandler<PacketTcomAction, IMessage> {

  private Mats mat = null;
  private int enchant = -1;

  public PacketTcomAction() {
  }

  public PacketTcomAction(TileTcom tile, Mats mat) {
    super(tile);
    this.mat = mat;
  }

  public PacketTcomAction(TileTcom tile, int enchant) {
    super(tile);
    this.enchant = enchant;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if (mat != null) {
      buf.writeByte(0);
      buf.writeByte(mat.ordinal());
    } else {
      buf.writeByte(1);
      buf.writeShort(enchant);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    byte command = buf.readByte();
    if (command == 0) {
      mat = Mats.values()[buf.readByte()];
    } else if (command == 1) {
      enchant = buf.readShort();
    } else {
      throw new InvalidParameterException();
    }
  }

  @Override
  public IMessage onMessage(PacketTcomAction message, MessageContext ctx) {
    TileTcom tile = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
    if (tile == null) {
      return null;
    }
    if (message.mat != null) {
      tile.extractItems(message.mat);
    } else {
      tile.extractEnchantment(message.enchant);
    }
    return null;
  }
}