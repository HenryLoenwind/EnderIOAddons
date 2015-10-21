package info.loenwind.enderioaddons.machine.tcom;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.enderioaddons.machine.tcom.engine.EngineTcom;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketTcomUpdate extends MessageTileEntity<TileTcom> implements IMessageHandler<PacketTcomUpdate, IMessage> {

  /*
   * Note: We are using nbt target ITEM for full updates and CLIENT for small
   * updates (mats and a boolean only)
   */

  private NBTTagCompound nbtRoot;

  public PacketTcomUpdate() {
  }

  public PacketTcomUpdate(TileTcom tile, boolean full) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if (tile != null) {
      EngineTcom engine = tile.engine;
      if (engine != null) {
        Writer.write(full ? ITEM : CLIENT, nbtRoot, engine);
      }
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
  public IMessage onMessage(PacketTcomUpdate message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player != null) {
      TileTcom te = message.getTileEntity(player.worldObj);
      if (te != null && te.engine != null) {
        Reader.read(ITEM, message.nbtRoot, te.engine);
        player.worldObj.func_147479_m(message.x, message.y, message.z); // markBlockForRenderUpdate()
      }
    }
    return null;
  }
}