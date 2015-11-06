package info.loenwind.enderioaddons;

import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.config.ConfigHandler;
import info.loenwind.enderioaddons.network.Manager;
import info.loenwind.enderioaddons.recipe.Recipes;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.Lang;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EnderIOAddons.MODID, name = EnderIOAddons.MOD_NAME, version = EnderIOAddons.VERSION, dependencies = "required-after:EnderIO;after:waterhooks", guiFactory = "info.loenwind.enderioaddons.config.gui.ConfigFactory")
public class EnderIOAddons implements InitAware {
  @Nonnull
  public static final String MODID = "enderioaddons";
  @SuppressWarnings("null")
  @Nonnull
  public static final String DOMAIN = MODID.toLowerCase(Locale.US);
  @Nonnull
  public static final String MOD_NAME = "Ender IO Addons";
  @Nonnull
  public static final String VERSION = "@VERSION@";
  @Nonnull
  public static final String ENDERIO_VERSION = "@enderio_version@";
  @Nonnull
  public static final String UPDATE_URL = "http://enderioaddons.loenwind.info/versioninfo_" + ENDERIO_VERSION + ".json?" + VERSION;

  @SidedProxy(clientSide = "info.loenwind.enderioaddons.proxy.ClientOnlyProxy", serverSide = "info.loenwind.enderioaddons.proxy.ClientAndServerProxy")
  public static InitAware proxy;
  @Nonnull
  public static final InitAware config = new ConfigHandler();
  @Nonnull
  public static final InitAware recipes = new Recipes();
  @Nonnull
  public static final Manager network = new Manager();
  @Nonnull
  public static final Lang lang = new Lang(DOMAIN);

  @Override
  @EventHandler
  public void init(FMLPreInitializationEvent event) {
    config.init(event);
    proxy.init(event);
    recipes.init(event);
    network.init(event);
  }

  @Override
  @EventHandler
  public void init(FMLInitializationEvent event) {
    FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addVersionCheck", UPDATE_URL);
    config.init(event);
    proxy.init(event);
    recipes.init(event);
    network.init(event);
  }

  @Override
  @EventHandler
  public void init(FMLPostInitializationEvent event) {
    config.init(event);
    proxy.init(event);
    recipes.init(event);
    network.init(event);
    // info.loenwind.enderioaddons.machine.waterworks.engine.BootstrapHelper.dumpConfig();
  }

}
