package info.loenwind.enderioaddons.common;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.SlotDefinition;

@Storable
public abstract class TileEnderIOAddons extends AbstractPoweredTaskEntity {

  public TileEnderIOAddons(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  /**
   * This stops read/writeCommon(). It should be set when calling
   * super.read/writeCustom() as it calls read/writeCommon(). This doesn't mesh
   * well with our 3-tier nbt-handling.
   * 
   * <p>
   * Remember:
   * 
   * <ul>
   * <li>packet handler calls custom,
   * <li>save/load calls custom,
   * <li>reading from/writing to itemstack calls common,
   * <li>custom calls common
   * </ul>
   */
  private boolean noCommon = false;

  // Note: The "final"s are only to catch errors while converting classes.

  @Override
  public final void readCustomNBT(NBTTagCompound nbtRoot) {
    try {
      noCommon = true;
      super.readCustomNBT(nbtRoot);
    } finally {
      noCommon = false;
    }
    Reader.read(SAVE, nbtRoot, this);
  }

  @Override
  public final void writeCustomNBT(NBTTagCompound nbtRoot) {
    try {
      noCommon = true;
      super.writeCustomNBT(nbtRoot);
    } finally {
      noCommon = false;
    }
    Writer.write(SAVE, nbtRoot, this);
  }

  @Override
  public final void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    if (!noCommon) {
      Reader.read(ITEM, nbtRoot, this);
    }
  }

  @Override
  public final void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if (!noCommon) {
      Writer.write(ITEM, nbtRoot, this);
    }
  }

  @Override
  public final Packet getDescriptionPacket() {
    NBTTagCompound nbtRoot = new NBTTagCompound();
    try {
      noCommon = true;
      super.writeCustomNBT(nbtRoot);
    } finally {
      noCommon = false;
    }
    Writer.write(CLIENT, nbtRoot, this);
    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbtRoot);
  }

  @Override
  public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    final NBTTagCompound nbtRoot = pkt.func_148857_g();
    Reader.read(CLIENT, nbtRoot, this);
    try {
      noCommon = true;
      super.readCustomNBT(nbtRoot);
    } finally {
      noCommon = false;
    }
  }

}