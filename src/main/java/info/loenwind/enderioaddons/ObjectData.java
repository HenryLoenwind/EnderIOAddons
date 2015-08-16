package info.loenwind.enderioaddons;

import java.lang.reflect.Field;
import java.util.Map;

import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.EnderIO;

public class ObjectData {

	public static int GUI_ID_DRAIN = 0;
	public static String blockDrain_unlocalisedName;

	private ObjectData() {
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

		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}
	
}
