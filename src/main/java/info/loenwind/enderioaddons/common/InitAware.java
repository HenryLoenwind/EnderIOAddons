package info.loenwind.enderioaddons.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public interface InitAware {

  void init(FMLPreInitializationEvent event);

  void init(FMLInitializationEvent event);

  void init(FMLPostInitializationEvent event);
}
