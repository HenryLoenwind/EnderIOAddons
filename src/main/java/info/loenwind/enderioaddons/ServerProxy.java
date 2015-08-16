package info.loenwind.enderioaddons;

import info.loenwind.enderioaddons.ObjectData;
import info.loenwind.enderioaddons.drain.BlockDrain;
import info.loenwind.enderioaddons.drain.InfiniteWaterSourceStopper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements Proxy {

	@Override
	public void init(FMLPreInitializationEvent event) {
		ObjectData.compute_GUI_IDs();
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
