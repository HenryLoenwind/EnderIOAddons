package info.loenwind.enderioaddons.machine.afarm;

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
    } catch (Throwable t) {
      hasAgriAPI = hasAgri = false;
    }
    System.out.println("Agri: " + hasAgriAPI + "/" + hasAgri);
  }

}
