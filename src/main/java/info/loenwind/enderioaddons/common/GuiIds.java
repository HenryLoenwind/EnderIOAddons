package info.loenwind.enderioaddons.common;

import java.lang.reflect.Field;
import java.util.Map;

import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.EnderIO;

public class GuiIds {

  public static int GUI_ID_DRAIN = 0;

  private GuiIds() {
  }

  static void compute_GUI_IDs() {
    GUI_ID_DRAIN = nextID();
  }

  static private int nextID() {

    try {
      Field field = EnderIO.guiHandler.getClass().getDeclaredField("guiHandlers");
      field.setAccessible(true);
      Map<Integer, IGuiHandler> guiHandlers = (Map<Integer, IGuiHandler>) field.get(EnderIO.guiHandler);

      int i = crazypants.enderio.GuiHandler.GUI_ID_CAP_BANK;
      while (i < Integer.MAX_VALUE) {
        i++;
        if (!guiHandlers.containsKey(i)) {
          return i;
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return -1;
  }

}
