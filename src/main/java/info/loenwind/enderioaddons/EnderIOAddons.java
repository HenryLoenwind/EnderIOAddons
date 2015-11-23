package info.loenwind.enderioaddons;

import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.config.ConfigHandler;
import info.loenwind.enderioaddons.network.Manager;
import info.loenwind.enderioaddons.recipe.Recipes;

import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.Lang;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

@Mod(modid = EnderIOAddons.MODID, name = EnderIOAddons.MOD_NAME, version = EnderIOAddons.VERSION, dependencies = "required-after:EnderIO;after:waterhooks;after:AgriCraft", guiFactory = "info.loenwind.enderioaddons.config.gui.ConfigFactory")
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

  public static boolean mode24 = true;

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

    Calendar cal = Calendar.getInstance(Locale.getDefault());
    if (cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) > 10 && cal.get(Calendar.DAY_OF_MONTH) <= 26) {
      FMLCommonHandler.instance().bus().register(new ModeTicker());
    }
  }

  private final class ModeTicker {
    @SubscribeEvent
    public void onTick(ServerTickEvent evt) {
      if (evt.phase == Phase.END) {
        onTick();
      }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent evt) {
      if (evt.phase == Phase.END) {
        onTick();
      }
    }

    private void onTick() {
      mode24 = false;
      final Calendar cal = Calendar.getInstance(Locale.getDefault());
      if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day == 24 && cal.get(Calendar.HOUR_OF_DAY) > 12) {
          mode24 = true;
        } else if (day >= 26 && day <= 26) {
          mode24 = true;
        }
      }
    }
  }

}
