package info.loenwind.enderioaddons.network;

import info.loenwind.enderioaddons.common.InitAware;

import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;

public class Manager implements InitAware {

  public Manager() {
  }

  @Override
  public void init(FMLPreInitializationEvent event) {
    PacketHandler.INSTANCE.registerMessage(PacketParticles.class, PacketParticles.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSlotVisibility.class, PacketSlotVisibility.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkUpdate.class, PacketNetworkUpdate.class, PacketHandler.nextID(), Side.CLIENT);
  }

  @Override
  public void init(FMLInitializationEvent event) {
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
    FMLCommonHandler.instance().bus().register(this);
  }

  private Map<INetworkUpdatable, Integer> networkUpdatables = new WeakHashMap<>();
  private Map<INetworkUpdatable, int[]> networkUpdatableData = new WeakHashMap<>();

  public void register(INetworkUpdatable te) {
    if (!((TileEntity) te).getWorldObj().isRemote) {
      networkUpdatables.put(te, te.getNetworkUpdateCheckInterval());
      networkUpdatableData.put(te, new int[te.getNetworkUpdateCount()]);
    }
  }

  @SubscribeEvent
  public void onTick(ServerTickEvent evt) {
    if (evt.phase == Phase.END) {
      for (Entry<INetworkUpdatable, Integer> entry : networkUpdatables.entrySet()) {
        final INetworkUpdatable te = entry.getKey();
        if (te != null) {
          int i = entry.getValue();
          if (i-- <= 0) {
            int[] data = networkUpdatableData.get(te);
            boolean needUpdate = false;
            for (int j = 0; j < data.length; j++) {
              int value = te.getNetworkUpdate(j);
              if (value != data[j]) {
                data[j] = value;
                needUpdate = true;
              }
            }
            if (needUpdate) {
              PacketHandler.sendToAllAround(new PacketNetworkUpdate((TileEntity) te, data), (TileEntity) te);
            }
            entry.setValue(te.getNetworkUpdateCheckInterval());
          } else {
            entry.setValue(i);
          }
        }
      }
    }
  }

}
