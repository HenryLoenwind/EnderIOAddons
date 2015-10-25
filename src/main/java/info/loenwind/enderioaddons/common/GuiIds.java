package info.loenwind.enderioaddons.common;

import java.lang.reflect.Field;
import java.util.Map;

import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.EnderIO;

public class GuiIds {

  public static int GUI_ID_DRAIN = 0;
  public static int GUI_ID_COBBLEWORKS = 0;
  public static int GUI_ID_WATERWORKS = 0;
  public static int GUI_ID_IHOPPER = 0;
  public static int GUI_ID_NIARD = 0;
  public static int GUI_ID_VOIDTANK = 0;
  public static int GUI_ID_PMON = 0;
  public static int GUI_ID_TCOM = 0;
  public static int GUI_ID_MAGCHARGER = 0;

  private GuiIds() {
  }

  public static void compute_GUI_IDs() {
    GUI_ID_DRAIN = nextID();
    GUI_ID_COBBLEWORKS = nextID();
    GUI_ID_WATERWORKS = nextID();
    GUI_ID_IHOPPER = nextID();
    GUI_ID_NIARD = nextID();
    GUI_ID_VOIDTANK = nextID();
    GUI_ID_PMON = nextID();
    GUI_ID_TCOM = nextID();
    GUI_ID_MAGCHARGER = nextID();
  }

  private static int lastId = crazypants.enderio.GuiHandler.GUI_ID_CAP_BANK;

  static private int nextID() {

    try {
      Field field = EnderIO.guiHandler.getClass().getDeclaredField("guiHandlers");
      field.setAccessible(true);
      Map<Integer, IGuiHandler> guiHandlers = (Map<Integer, IGuiHandler>) field.get(EnderIO.guiHandler);

      while (++lastId > 0) {
        if (!guiHandlers.containsKey(lastId)) {
          return lastId;
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return -1;
  }

}
