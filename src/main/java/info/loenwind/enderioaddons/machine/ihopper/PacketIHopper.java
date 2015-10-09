package info.loenwind.enderioaddons.machine.ihopper;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketIHopper extends MessageTileEntity<TileIHopper> implements IMessageHandler<PacketIHopper, IMessage> {

  private int slot;
  private ItemStack stack;

  public PacketIHopper() {
  }

  private PacketIHopper(TileIHopper tile) {
    super(tile);
  }

  public static PacketIHopper setGhostSlotContents(TileIHopper te, int slot, ItemStack stack) {
    PacketIHopper msg = new PacketIHopper(te);
    msg.slot = slot;
    msg.stack = stack;
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    slot = buf.readShort();
    stack = ByteBufUtils.readItemStack(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(slot);
    ByteBufUtils.writeItemStack(buf, stack);
  }

  @Override
  public IMessage onMessage(PacketIHopper msg, MessageContext ctx) {
    TileIHopper te = msg.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
    if (te != null) {
      te.setGhostSlotContents(msg.slot, msg.stack);
    }
    return null;
  }

}
