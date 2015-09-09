package info.loenwind.enderioaddons.config;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.common.Log;
import io.netty.buffer.ByteBuf;

import java.io.File;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;

public class Config implements InitAware {

  public static int drainContinuousEnergyUseRF = ConfigValues.drainContinuousEnergyUseRF.getDefaultInt();
  public static int drainPerBucketEnergyUseRF = ConfigValues.drainPerBucketEnergyUseRF.getDefaultInt();
  public static int drainPerSourceBlockMoveEnergyUseRF = ConfigValues.drainPerSourceBlockMoveEnergyUseRF.getDefaultInt();
  public static boolean drainAllowOnDedicatedServer = ConfigValues.drainAllowOnDedicatedServer.getDefaultBoolean();
  public static boolean drainEnabled = ConfigValues.drainEnabled.getDefaultBoolean();

  public static int cobbleWorksRfPerCobblestone = ConfigValues.cobbleWorksRfPerCobblestone.getDefaultInt();
  public static int cobbleWorksRfDiscountForCrafting = ConfigValues.cobbleWorksRfDiscountForCrafting.getDefaultInt(); // %
  public static int cobbleWorksRfDiscountForSmelting = ConfigValues.cobbleWorksRfDiscountForSmelting.getDefaultInt(); // %
  public static int cobbleWorksRfDiscountForCrushing = ConfigValues.cobbleWorksRfDiscountForCrushing.getDefaultInt(); // %
  public static int cobbleWorksRfDiscountPerUpgrade = ConfigValues.cobbleWorksRfDiscountPerUpgrade.getDefaultInt(); // %

  public static int waterWorksWaterReductionPercentage = 10; // TODO
  public static float waterWorksRFperTask1 = 5 * 20 * 20; // TODO
  public static float waterWorksRFperTask2 = 5 * 20 * 20 * 1.75f * 0.9f; // TODO
  public static float waterWorksRFperTask3 = 5 * 20 * 20 * 3 * 0.8f; // TODO

  public static float waterWorksLiquidFactorperTask1 = 1.0f; // TODO
  public static float waterWorksLiquidFactorperTask2 = 1.75f; // TODO
  public static float waterWorksLiquidFactorperTask3 = 3.0f; // TODO

  public static int waterWorksRFusePerTick1 = 20; // TODO
  public static int waterWorksRFusePerTick2 = 40; // TODO
  public static int waterWorksRFusePerTick3 = 60; // TODO

  public static int waterWorksRFinPerTick1 = 100; // TODO
  public static int waterWorksRFinPerTick2 = 200; // TODO
  public static int waterWorksRFinPerTick3 = 800; // TODO

  //	****************************************************************************************

  public static Configuration configuration;
  public static File configDirectory;

  public Config() {
  }

  public void init(FMLPreInitializationEvent event) {
    PacketHandler.INSTANCE.registerMessage(PacketConfigSync.class, PacketConfigSync.class, PacketHandler.nextID(), Side.CLIENT);
    FMLCommonHandler.instance().bus().register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderIOAddons.MODID.toLowerCase());
    if (!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, EnderIOAddons.MODID + ".cfg");
    configuration = new Configuration(configFile);
    syncConfig(false);
  }

  private static void syncConfig(boolean load) {
    try {
      if (load) {
        configuration.load();
      }
      processConfig();
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
    computeDerivedValues(false);
    if (configuration.hasChanged()) {
      configuration.save();
    }
  }

  private static void computeDerivedValues(boolean serverSync) {
    if (serverSync) {
      drainEnabled = drainEnabled && drainAllowOnDedicatedServer;
    } else {
      drainEnabled = drainEnabled && (drainAllowOnDedicatedServer || FMLCommonHandler.instance().getSide().isClient());
    }
  }

  public static void toBytes(ByteBuf buf) {
    ConfigValues.toBytes(buf);
  }

  public static void fromBytes(ByteBuf buf) {
    ConfigValues.fromBytes(buf);
    computeDerivedValues(true);
  }

  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    PacketHandler.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
  }

  @SubscribeEvent
  public void onPlayerLogout(ClientDisconnectionFromServerEvent event) {
    syncConfig(false);
  }

  @Override
  public void init(FMLInitializationEvent event) {
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
