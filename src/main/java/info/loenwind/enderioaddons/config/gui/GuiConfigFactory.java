package info.loenwind.enderioaddons.config.gui;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.ConfigHandler;
import info.loenwind.enderioaddons.config.Section;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import crazypants.enderio.EnderIO;

@SuppressWarnings({ "rawtypes" })
public class GuiConfigFactory extends GuiConfig {

  public GuiConfigFactory(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderIOAddons.MODID, false, false, EnderIO.lang.localize("config.title"));
  }

  private static List<IConfigElement> getConfigElements(@SuppressWarnings("unused") GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = EnderIO.lang.addPrefix("config.");

    for (Section section : Section.values()) {
      if (!section.sync || !ConfigHandler.configLockedByServer) {
        list.add(new ConfigElement<ConfigCategory>(ConfigHandler.configuration.getCategory(section.name).setLanguageKey(prefix)));
      }
    }

    return list;
  }
}
