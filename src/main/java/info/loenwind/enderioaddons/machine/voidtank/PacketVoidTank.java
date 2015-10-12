package info.loenwind.enderioaddons.machine.voidtank;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketVoidTank extends MessageTileEntity<TileVoidTank> implements IMessageHandler<PacketVoidTank, IMessage> {

  private NBTTagCompound nbtRoot;

  public PacketVoidTank() {
  }

  public PacketVoidTank(@Nonnull TileVoidTank tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.tank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.tank.writeToNBT(tankRoot);
      nbtRoot.setTag("tank", tankRoot);
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
  public IMessage onMessage(PacketVoidTank message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player != null) {
      TileVoidTank tile = message.getTileEntity(player.worldObj);
      if (tile == null) {
        return null;
      }
      if (message.nbtRoot.hasKey("tank")) {
        NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("tank");
        tile.tank.readFromNBT(tankRoot);
      } else {
        tile.tank.setFluid(null);
      }
    }
    return null;
  }
}