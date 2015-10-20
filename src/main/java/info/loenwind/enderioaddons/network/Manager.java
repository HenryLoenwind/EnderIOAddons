package info.loenwind.enderioaddons.network;

import info.loenwind.enderioaddons.common.InitAware;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;

public class Manager implements InitAware {

  public Manager() {
  }

  @Override
  public void init(FMLPreInitializationEvent event) {
    PacketHandler.INSTANCE.registerMessage(PacketParticles.class, PacketParticles.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSlotVisibility.class, PacketSlotVisibility.class, PacketHandler.nextID(), Side.SERVER);
  }

  @Override
  public void init(FMLInitializationEvent event) {
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
