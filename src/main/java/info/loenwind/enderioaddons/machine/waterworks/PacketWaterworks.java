package info.loenwind.enderioaddons.machine.waterworks;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.EnderIO;

public class PacketWaterworks extends MessageTileEntity<TileWaterworks> implements IMessageHandler<PacketWaterworks, IMessage> {

  private NBTTagCompound nbtRoot;

  public PacketWaterworks() {
  }

  public PacketWaterworks(TileWaterworks tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if (tile.tank1.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.tank1.writeToNBT(tankRoot);
      nbtRoot.setTag("tank1", tankRoot);
    }
    if (tile.tank2.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.tank2.writeToNBT(tankRoot);
      nbtRoot.setTag("tank2", tankRoot);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    nbtRoot = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  public IMessage onMessage(PacketWaterworks message, MessageContext ctx) {
    EntityPlayer player = ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity : EnderIO.proxy.getClientPlayer();
    TileWaterworks tile = message.getTileEntity(player.worldObj);
    if (tile == null) {
      return null;
    }
    if (message.nbtRoot.hasKey("inputTank")) {
      NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("tank1");
      tile.tank1.readFromNBT(tankRoot);
    } else {
      tile.tank1.setFluid(null);
    }
    if (message.nbtRoot.hasKey("outputTank")) {
      NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("tank2");
      tile.tank2.readFromNBT(tankRoot);
    } else {
      tile.tank2.setFluid(null);
    }
    return null;
  }

}
