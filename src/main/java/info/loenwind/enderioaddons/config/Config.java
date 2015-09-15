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

  public static int drainContinuousEnergyUseRF;
  public static int drainPerBucketEnergyUseRF;
  public static int drainPerSourceBlockMoveEnergyUseRF;
  public static boolean drainAllowOnDedicatedServer;
  public static boolean drainEnabled;

  public static int cobbleWorksRfPerCobblestone;
  public static int cobbleWorksRfDiscountForCrafting; // %
  public static int cobbleWorksRfDiscountForSmelting; // %
  public static int cobbleWorksRfDiscountForCrushing; // %
  public static int cobbleWorksRfDiscountPerUpgrade; // %
  public static boolean cobbleWorksEnabled;

  public static int waterWorksWaterReductionPercentage; // %
  public static double waterWorksRFperTask1;
  public static double waterWorksRFperTask2;
  public static double waterWorksRFperTask3;

  public static double waterWorksLiquidFactorperTask1;
  public static double waterWorksLiquidFactorperTask2;
  public static double waterWorksLiquidFactorperTask3;

  public static int waterWorksRFusePerTick1;
  public static int waterWorksRFusePerTick2;
  public static int waterWorksRFusePerTick3;

  public static int waterWorksRFinPerTick1;
  public static int waterWorksRFinPerTick2;
  public static int waterWorksRFinPerTick3;

  public static boolean waterWorksEnabled;

  static {
    ConfigValues.loadAll();
  }

  //	****************************************************************************************

  public static Configuration configuration;
  public static File configDirectory;
  public static boolean configLockedByServer = false;

  public Config() {
  }

  @Override
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

  @SuppressWarnings("static-method")
  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if (event.modID.equals(EnderIOAddons.MODID)) {
      Log.info("Updating config...");
      syncConfig(false);
    }
  }

  @SuppressWarnings("static-method")
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
    configLockedByServer = true;
  }

  @SuppressWarnings("static-method")
  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    PacketHandler.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
  }

  @SuppressWarnings("static-method")
  @SubscribeEvent
  public void onPlayerLogout(@SuppressWarnings("unused") ClientDisconnectionFromServerEvent event) {
    syncConfig(false);
    configLockedByServer = false;
  }

  @Override
  public void init(FMLInitializationEvent event) {
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
