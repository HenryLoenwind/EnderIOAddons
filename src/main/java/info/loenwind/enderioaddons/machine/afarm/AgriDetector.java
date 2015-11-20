package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.common.Log;

public class AgriDetector {

  public static boolean hasAgriAPI = false;
  public static boolean hasAgri = false;

  static {
    try {
      Class clazz = Class.forName("com.InfinityRaider.AgriCraft.api.API");
      hasAgriAPI = clazz != null;
      if (hasAgriAPI) {
        TileAfarm.detectAgri();
      }
      Log.info("Agricraft API is " + (hasAgriAPI ? "" : "not ") + "installed. Agricraft is " + (hasAgri ? "" : "not ")
          + "installed. AgriCarft Farming station is " + (hasAgri ? "" : "not ") + "available.");
    } catch (Throwable t) {
      hasAgriAPI = hasAgri = false;
      Log.info("Crashed while trying to find out if AgriCraft is installed. AgriCraft Farming station is not available.");
      t.printStackTrace();
    }
  }

  public static void registerPlants() {
    TileAfarm.registerPlants();
  }

}
