package info.loenwind.enderioaddons.machine.part;

import info.loenwind.enderioaddons.EnderIOAddons;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

public enum MachinePart {

  MACHINE_FRAME("machineFrame", true, true, true, null, false, false), //
  FRAME_TANK("frameTank", true, true, false, null, false, true), //
  FRAME_TANKS("frameTanks", true, true, false, null, true, false), //
  MACHINE_FRAME_TANK("machineFrameTank", true, true, true, null, true, false), //
  COBBLE_CONTROLLER("cobbleController", true, true, false, "cobbleController", false, false), //
  HEATING_ELEMENT("heatingElement", false, false, false, null, false, false), //
  FILTER_ELEMENT("filterElement", true, false, false, null, false, false), //
  WATER_CONTROLLER("waterController", true, true, false, "waterController", false, false); //

  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDict;
  public final boolean render3d;
  public final boolean renderAsFrameMachine;
  public final boolean hasFrame;
  public final String controllerModelName;
  public final boolean hasTanks;
  public final boolean hasSingleTank;

  private MachinePart(String unlocalisedName, boolean render3d, boolean renderAsFrameMachine, boolean hasFrame,
      String controllerModelName, boolean hasTanks, boolean hasSingleTank) {
    this.unlocalisedName = EnderIOAddons.DOMAIN + "." + unlocalisedName;
    this.iconKey = EnderIOAddons.DOMAIN + ":" + unlocalisedName;
    this.oreDict = "item" + StringUtils.capitalize(unlocalisedName);
    this.render3d = render3d;
    this.renderAsFrameMachine = renderAsFrameMachine;
    this.hasFrame = hasFrame;
    this.controllerModelName = controllerModelName;
    this.hasTanks = hasTanks;
    this.hasSingleTank = hasSingleTank;
  }
  
  public static void registerOres(Item item) {
    for (MachinePart m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }

  public String getControllerModelName() {
    return controllerModelName;
  }
}
