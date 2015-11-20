package info.loenwind.enderioaddons.machine.part;

import info.loenwind.enderioaddons.EnderIOAddons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

public enum MachinePart {

  MACHINE_FRAME("machineFrame", true, true, true, null, false, false, false), //
  FRAME_TANK("frameTank", true, true, false, null, false, true, false), //
  FRAME_TANKS("frameTanks", true, true, false, null, true, false, false), //
  MACHINE_FRAME_TANK("machineFrameTank", true, true, true, null, true, false, false), //
  COBBLE_CONTROLLER("cobbleController", true, true, false, "cobbleController", false, false, false), //
  HEATING_ELEMENT("heatingElement", false, false, false, null, false, false, false), //
  FILTER_ELEMENT("filterElement", true, false, false, null, false, false, false), //
  WATER_CONTROLLER("waterController", true, true, false, "waterController", false, false, false), //
  IHOPPER_CONTROLLER("ihopperController", true, true, false, "ihopperController", false, false, false), //
  TCOM_CONTROLLER("tcomController", true, true, false, "tcomController", false, false, false), //
  TRAY("tray", true, true, false, null, false, false, false), //
  PYLON("pylon", true, true, false, null, false, false, false), //
  PYLONTANK("pylontank", true, true, false, null, false, false, false), //
  CHASSIPARTS("chassiParts", false, false, false, null, false, false, false), //
  FLAGPARTS("flagparts", false, false, false, null, false, false, false), //
  SIMPLEMAGNET("simplemagnet", false, false, false, null, false, false, false), //
  AFARMINFO("afarminfo", false, false, false, null, false, false, false), //
  FCM_BASE("fcmBaseModule", false, false, false, null, false, false, true), //
  FCM_IQ("fcmIQModule", false, false, false, null, false, false, true), //
  RAKE_BR1("handRakeWoodBroken", false, false, false, null, false, false, true), //
  RAKE_BR2("handRakeIronBroken", false, false, false, null, false, false, true), //
  IRAKE("iRake", false, false, false, null, false, false, true), //
  SEED("darkseeds", false, false, false, null, false, false, true), //
  SCS("scs", false, false, false, null, false, false, true), //
  MCS("mcs", false, false, false, null, false, false, true), //
  LCS("lcs", false, false, false, null, false, false, true), //
  CLHP("clhp", false, false, false, null, false, false, true), //
  ;

  @Nonnull
  public final String unlocalisedName;
  @Nonnull
  public final String iconKey;
  @Nonnull
  public final String oreDict;
  public final boolean render3d;
  public final boolean renderAsFrameMachine;
  public final boolean hasFrame;
  @Nullable
  public final String controllerModelName;
  public final boolean hasTanks;
  public final boolean hasSingleTank;
  public final boolean isAgricraftPart;

  private MachinePart(@Nonnull String unlocalisedName, boolean render3d, boolean renderAsFrameMachine, boolean hasFrame, @Nullable String controllerModelName,
      boolean hasTanks, boolean hasSingleTank, boolean isAgricraftPart) {
    this.unlocalisedName = EnderIOAddons.DOMAIN + "." + unlocalisedName;
    this.iconKey = EnderIOAddons.DOMAIN + ":" + unlocalisedName;
    this.oreDict = "item" + StringUtils.capitalize(unlocalisedName);
    this.render3d = render3d;
    this.renderAsFrameMachine = renderAsFrameMachine;
    this.hasFrame = hasFrame;
    this.controllerModelName = controllerModelName;
    this.hasTanks = hasTanks;
    this.hasSingleTank = hasSingleTank;
    this.isAgricraftPart = isAgricraftPart;
  }

  public static void registerOres(@Nonnull Item item) {
    for (MachinePart m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }

  public String getControllerModelName() {
    return controllerModelName;
  }

  public static boolean isAgri(int id) {
    return values()[id].isAgricraftPart;
  }
}
