package info.loenwind.enderioaddons.proxy;

import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.fluid.Fluids;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.InfiniteWaterSourceStopper;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientAndServerProxy implements InitAware {

  @Override
  public void init(FMLPreInitializationEvent event) {
    GuiIds.compute_GUI_IDs();
    Fluids.init(event);
    BlockDrain.create();
    AbstractBlockFramework.create();
    BlockCobbleworks.create();
    BlockWaterworks.create();
    BlockIHopper.create();
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
