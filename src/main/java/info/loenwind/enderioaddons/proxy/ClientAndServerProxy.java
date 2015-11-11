package info.loenwind.enderioaddons.proxy;

import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.fluid.Fluids;
import info.loenwind.enderioaddons.gui.PacketAdvancedRedstoneMode;
import info.loenwind.enderioaddons.machine.afarm.BlockAfarm;
import info.loenwind.enderioaddons.machine.afarm.item.ItemModule;
import info.loenwind.enderioaddons.machine.chassis.BlockChassis;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.InfiniteWaterSourceStopper;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.magcharger.BlockMagCharger;
import info.loenwind.enderioaddons.machine.niard.BlockNiard;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.pmon.BlockPMon;
import info.loenwind.enderioaddons.machine.tcom.BlockTcom;
import info.loenwind.enderioaddons.machine.voidtank.BlockVoidTank;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;

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
    BlockNiard.create();
    BlockVoidTank.create();
    BlockPMon.create();
    BlockTcom.create();
    BlockFlag.create();
    BlockMagCharger.create();
    BlockChassis.create();
    BlockAfarm.create();
    ItemModule.create();
  }

  @Override
  public void init(FMLInitializationEvent event) {
    InfiniteWaterSourceStopper.register();
    PacketHandler.INSTANCE.registerMessage(PacketAdvancedRedstoneMode.class, PacketAdvancedRedstoneMode.class, PacketHandler.nextID(), Side.SERVER);
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
