package info.loenwind.enderioaddons.config;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.common.Log;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config implements InitAware {

  public static int drainContinuousEnergyUseRF = ConfigValues.drainContinuousEnergyUseRF.getDefaultInt();
  public static int drainPerBucketEnergyUseRF = ConfigValues.drainPerBucketEnergyUseRF.getDefaultInt();
  public static int drainPerSourceBlockMoveEnergyUseRF = ConfigValues.drainPerSourceBlockMoveEnergyUseRF.getDefaultInt();
  public static boolean drainAllowOnDedicatedServer = ConfigValues.drainAllowOnDedicatedServer.getDefaultBoolean();
  public static boolean drainEnabled = ConfigValues.drainEnabled.getDefaultBoolean();

  //	****************************************************************************************

  public static Configuration configuration;
  public static File configDirectory;

  public Config() {
  }

  public void init(FMLPreInitializationEvent event) {
    FMLCommonHandler.instance().bus().register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderIOAddons.MODID.toLowerCase());
    if (!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderIOAddons.cfg");
    configuration = new Configuration(configFile);
    syncConfig(false);
  }

  private static void syncConfig(boolean load) {
    try {
      if (load) {
        configuration.load();
      }
      Config.processConfig();
    } catch (Exception e) {
      Log.error("EnderIOAddons has a problem loading it's configuration");
      e.printStackTrace();
    } finally {
      if (configuration.hasChanged()) {
        configuration.save();
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
    ConfigValues.loadAll(configuration);

    drainEnabled = drainEnabled && (drainAllowOnDedicatedServer || FMLCommonHandler.instance().getSide().isClient());

    if (configuration.hasChanged()) {
      configuration.save();
    }
  }

  @Override
  public void init(FMLInitializationEvent event) {
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
