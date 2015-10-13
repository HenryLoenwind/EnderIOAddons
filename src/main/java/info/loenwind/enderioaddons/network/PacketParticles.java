package info.loenwind.enderioaddons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;

public class PacketParticles implements IMessage, IMessageHandler<PacketParticles, IMessage> {

  private String particle;
  private double posx, posy, posz, velx, vely, velz;

  public PacketParticles() {
  }

  public PacketParticles(String particle, double x, double y, double z, double velx, double vely, double velz) {
    this.particle = particle;
    this.posx = x;
    this.posy = y;
    this.posz = z;
    this.velx = velx;
    this.vely = vely;
    this.velz = velz;
  }

  public static void spawnParticle(World world, String particle, BlockCoord bc, double velx, double vely, double velz) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketParticles(particle, bc.x, bc.y, bc.z, velx, vely, velz), new TargetPoint(world.provider.dimensionId, bc.x,
        bc.y, bc.z, 24));
  }

  public static void spawnParticle(World world, String particle, double x, double y, double z, double velx, double vely, double velz) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketParticles(particle, x, y, z, velx, vely, velz), new TargetPoint(world.provider.dimensionId, x, y, z, 24));
  }

  @Override
  public void toBytes(ByteBuf buf) {
    NetworkUtil.writeByteArray(buf, particle.getBytes());
    buf.writeDouble(posx);
    buf.writeDouble(posy);
    buf.writeDouble(posz);
    buf.writeDouble(velx);
    buf.writeDouble(vely);
    buf.writeDouble(velz);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    particle = new String(NetworkUtil.readByteArray(buf));
    posx = buf.readDouble();
    posy = buf.readDouble();
    posz = buf.readDouble();
    velx = buf.readDouble();
    vely = buf.readDouble();
    velz = buf.readDouble();
  }

  @Override
  public IMessage onMessage(PacketParticles message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player != null) {
      player.worldObj.spawnParticle(message.particle, message.posx, message.posy, message.posz, message.velx, message.vely, message.velz);
    }
    return null;
  }
}