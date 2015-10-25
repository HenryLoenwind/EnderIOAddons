package info.loenwind.enderioaddons.machine.flag;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.EnderIOAddons;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.config.Config;

@Storable
public class TileFlag extends TileEntityEio {

  @Store
  private BlockCoord parent = null;
  @SideOnly(Side.CLIENT)
  private long renderAge = -1;
  @Store({ CLIENT })
  private boolean isFreshlyPlaced = true;
  @Store
  private boolean isCharged = true;

  /*
   * States:
   * 
   * renderAge == -1 && isFreshlyPlaced && client
   * 
   * No data from server yet
   * 
   * renderAge == -1 && isFreshlyPlaced && server
   * 
   * Freshly placed and not yet notified the client
   * 
   * renderAge == -1 && !isFreshlyPlaced && server
   * 
   * Client has been notified
   * 
   * renderAge >= 0 && client
   * 
   * We know our age because the server told us
   * 
   * renderAge == -1 && !isFreshlyPlaced && client
   * 
   * We don't know our age, but the server told us we are not new
   */

  public TileFlag() {
    super();
  }

  @Override
  public final void readCustomNBT(NBTTagCompound nbtRoot) {
    Reader.read(SAVE, nbtRoot, this);
    isFreshlyPlaced = false;
  }

  @Override
  public final void writeCustomNBT(NBTTagCompound nbtRoot) {
    Writer.write(SAVE, nbtRoot, this);
  }

  @Override
  public final Packet getDescriptionPacket() {
    NBTTagCompound nbtRoot = new NBTTagCompound();
    Writer.write(CLIENT, nbtRoot, this);
    isFreshlyPlaced = false;
    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbtRoot);
  }

  @Override
  public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    final NBTTagCompound nbtRoot = pkt.func_148857_g();
    Reader.read(CLIENT, nbtRoot, this);
    if (isFreshlyPlaced) {
      resetRenderAge(0);
    }
  }

  @SideOnly(Side.CLIENT)
  public void resetRenderAge(int offset) {
    renderAge = EnderIO.proxy.getTickCount() - offset;
  }

  public void writeItemStackNBT(ItemStack itemstack) {
    if (itemstack.stackTagCompound == null) {
      itemstack.stackTagCompound = new NBTTagCompound();
    }
    Writer.write(ITEM, itemstack.stackTagCompound, this);
    if (parent != null) {
      itemstack.setStackDisplayName(EnderIOAddons.lang.localizeExact(itemstack.getUnlocalizedName() + ".name")
          + EnderIOAddons.lang.localize("flag.itemname.parent", parent.x, parent.y, parent.z));
    } else if (!isCharged) {
      itemstack.setStackDisplayName(EnderIOAddons.lang.localizeExact(itemstack.getUnlocalizedName() + ".name")
          + EnderIOAddons.lang.localize("flag.itemname.uncharged"));
    }
  }

  public void reparentItemstack(ItemStack itemstack) {
    if (itemstack.stackTagCompound == null) {
      itemstack.stackTagCompound = new NBTTagCompound();
    }
    BlockCoord tmp = parent;
    parent = getLocation();
    Writer.write(ITEM, itemstack.stackTagCompound, this);
    itemstack.setStackDisplayName(EnderIOAddons.lang.localizeExact(itemstack.getUnlocalizedName() + ".name")
        + EnderIOAddons.lang.localize("flag.itemname.parent", parent.x, parent.y, parent.z));
    parent = tmp;
  }

  public void readItemStackNBT(ItemStack itemstack) {
    if (itemstack.stackTagCompound != null) {
      Reader.read(ITEM, itemstack.stackTagCompound, this);
    }
    isFreshlyPlaced = true;
  }

  @Override
  protected boolean shouldUpdate() {
    return false;
  }

  public @Nullable BlockCoord getParent() {
    return parent;
  }

  @SideOnly(Side.CLIENT)
  public int getRenderAge() {
    if (renderAge < 0) {
      if (isFreshlyPlaced) {
        return 0;
      } else {
        return -1;
      }
    } else {
      return (int) (EnderIO.proxy.getTickCount() - renderAge);
    }
  }

  private static final String[] soundsIds = { "machine.flag", "machine.flag.fail" };
  private static final int[] soundDurations = { 81, 3 }; // 4.045s
  private final long[] lastSoundTick = new long[soundsIds.length];

  public void playSound(int id) {
    if (crazypants.enderio.config.Config.machineSoundsEnabled && lastSoundTick[id] <= EnderIO.proxy.getTickCount()) {
      lastSoundTick[id] = EnderIO.proxy.getTickCount() + soundDurations[id];
      getWorldObj().playSound(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, EnderIOAddons.DOMAIN + ":" + soundsIds[id], Config.machineSoundVolume, 1.0f, false);
    }
  }

  public boolean isCharged() {
    return isCharged;
  }

  public void setCharged(boolean isCharged) {
    this.isCharged = isCharged;
    if (!isCharged) {
      parent = null;
    }
  }

}
