package info.loenwind.enderioaddons.machine.framework;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.Log;

import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;

@Storable
public abstract class AbstractTileFramework extends AbstractPowerConsumerEntity {

  public static ResourceLocation getSoundFor(String sound) {
    return sound == null ? null : new ResourceLocation(EnderIOAddons.DOMAIN + ":" + sound);
  }

  public AbstractTileFramework(SlotDefinition slotDefinition) {
    super(slotDefinition);
    try {
      Field field = AbstractMachineEntity.class.getDeclaredField("soundRes");
      field.setAccessible(true);
      field.set(this, getSoundFor(getSoundName()));
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      Log.warn("Failed to access sound filed inherited from Ender IO: " + e);
    }
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    if (isSideDisabled(var1)) {
      return new int[0];
    }

    int[] res = new int[inventory.length - slotDefinition.getNumUpgradeSlots()];
    int index = 0;
    for (int i = 0; i < inventory.length; i++) {
      if (!slotDefinition.isUpgradeSlot(i)) {
        res[index] = i;
        index++;
      }
    }
    return res;
  }

  @Override
  public void setCapacitor(Capacitors capacitorType) {
    if (getCapacitorType() != capacitorType || getCapacitor() == capacitorType.capacitor) {
      super.setCapacitor(capacitorType);
    }
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