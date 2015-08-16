package info.loenwind.enderioaddons.config;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.Log;

import java.io.File;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class Config {

	public static int drainContinuousEnergyUseRF = ConfigValues.drainContinuousEnergyUseRF.getDefaultInt();
	public static int drainPerBucketEnergyUseRF = ConfigValues.drainPerBucketEnergyUseRF.getDefaultInt();
	public static int drainPerSourceBlockMoveEnergyUseRF = ConfigValues.drainPerSourceBlockMoveEnergyUseRF.getDefaultInt();
	public static boolean drainAllowOnDedicatedServer = ConfigValues.drainAllowOnDedicatedServer.getDefaultBoolean();
	public static boolean drainEnabled = false;

	//	****************************************************************************************
	
	public static Configuration config;
	public static File configDirectory;

	private Config() {}

	public static void init(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new Config());
		configDirectory = new File(event.getModConfigurationDirectory(), EnderIOAddons.MODID.toLowerCase());
		if(!configDirectory.exists()) {
			configDirectory.mkdir();
		}

		File configFile = new File(configDirectory, "EnderIOAddons.cfg");
		config = new Configuration(configFile);
		syncConfig(false);
	}

	public static void syncConfig(boolean load) {
		try {
			if (load) {
				config.load();
			}
			Config.processConfig();
		} catch (Exception e) {
			Log.error("EnderIOAddons has a problem loading it's configuration");
			e.printStackTrace();
		} finally {
			if(config.hasChanged()) {
				config.save();
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.modID.equals(EnderIOAddons.MODID)) {
			Log.info("Updating config...");
			syncConfig(false);
		}
	}

	@SubscribeEvent
	public void onConfigFileChanged(ConfigFileChangedEvent event) {
		if (event.modID.equals(EnderIOAddons.MODID)) {
			Log.info("Updating config...");
			syncConfig(true);
			event.setSuccessful();
		}
	}


	private static void processConfig() {
		ConfigValues.loadAll(config);

		drainEnabled = drainAllowOnDedicatedServer || !FMLCommonHandler.instance().getSide().isClient();
		
		if (config.hasChanged()) {
			config.save();
		}
	}


}
