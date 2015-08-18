package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.InfiniteWaterSourceStopper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements InitAware {

	@Override
	public void init(FMLPreInitializationEvent event) {
		GuiIds.compute_GUI_IDs();
    	BlockDrain.create();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		InfiniteWaterSourceStopper.register();
	}

	@Override
	public void init(FMLPostInitializationEvent event) {
	}

}
