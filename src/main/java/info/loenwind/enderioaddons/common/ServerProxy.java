package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.InfiniteWaterSourceStopper;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements InitAware {

  @Override
  public void init(FMLPreInitializationEvent event) {
    GuiIds.compute_GUI_IDs();
    Fluids.init(event);
    BlockDrain.create();
    AbstractBlockFramework.create();
    BlockCobbleworks.create();
    BlockWaterworks.create();
    ItemMachinePart.create();
  }

  @Override
  public void init(FMLInitializationEvent event) {
    InfiniteWaterSourceStopper.register();
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
