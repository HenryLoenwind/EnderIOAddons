package info.loenwind.enderioaddons.config;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public enum Config {

  drainContinuousEnergyUseRF(Section.DRAIN, 10, "The amount of power used by a (food) drain per tick", true, false), // 
  drainPerBucketEnergyUseRF(Section.DRAIN, 400, "The amount of power used by a (food) drain per 1000mB of liquid collected"), //
  drainPerSourceBlockMoveEnergyUseRF(Section.DRAIN, 250, "The amount of power used by a (food) drain to move a source block by one mete."), //
  drainAllowOnDedicatedServer(Section.DRAIN, false, "Allow the use of the drain on a dedicated server. This does not effect the food drain", true, true), //
  drainEnabled(Section.RECIPES, true, "Enable the crafting recipe for the drain", true, true), //

  foodDrainCollectsMilkFromCows(Section.DRAIN, true, "Allows the food drain to collect milk from cows."), //
  foodDrainCollectsFlowingMilk(Section.DRAIN, true, "Allows the food drain to pick up flowing milk."), //
  foodDrainRange(Section.DRAIN, 3d, "Distance from the food drain that is searched for cows."), //
  foodDrainEnabled(Section.RECIPES, true, "Enable the crafting recipe for the food drain", true, true), //

  cobbleWorksRfPerCobblestone(Section.COBBLEWORKS, 100, "RF used per generated cobblestone", true, false), //

  cobbleWorksRfDiscountForCrafting(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of crafting operations in percent (0-100)"), //
  cobbleWorksRfDiscountForSmelting(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of smelting operations in percent (0-100)"), //
  cobbleWorksRfDiscountForCrushing(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of crusing (sagmilling) operations in percent (0-100)"), //
  cobbleWorksRfDiscountPerUpgrade(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of any operation in percent (0-100). This discount is applied once for a double-layer capacitor "
          + "and twice for an octadic capacitor upgrade."), //

  cobbleWorksEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Cobbleworks", true, true), //

  waterWorksWaterReductionPercentage(Section.WATERWORKS, 10, "Amount of liquid produced when processing in percent (0-100) of the input liquid", true, false), //

  waterWorksRFperTask1(Section.WATERWORKS, 5 * 20 * 20 * 1.0d, "RF per task (machine with no capacitor upgrades)", true, false), //
  waterWorksRFperTask2(Section.WATERWORKS, 5 * 20 * 20 * 1.75f * 0.9d, "RF per task (machine with first capacitor upgrade)", true, false), //
  waterWorksRFperTask3(Section.WATERWORKS, 5 * 20 * 20 * 3 * 0.8d, "RF per task (machine with second capacitor upgrade)", true, false), //

  waterWorksLiquidFactorperTask1(Section.WATERWORKS, 1.0d,
      "Amount of input liquid consumed per task as factor on the base value of 1000mB (machine with no capacitor upgrades)", true, false), //
  waterWorksLiquidFactorperTask2(Section.WATERWORKS, 1.75d,
      "Amount of input liquid consumed per task as factor on the base value of 1000mB (machine with first capacitor upgrade)", true, false), //
  waterWorksLiquidFactorperTask3(Section.WATERWORKS, 3.0d,
      "Amount of input liquid consumed per task as factor on the base value of 1000mB (machine with second capacitor upgrade)", true, false), //

  waterWorksRFusePerTick1(Section.WATERWORKS, 20, "RF a machine can use per tick (machine with no capacitor upgrades)", true, false), //
  waterWorksRFusePerTick2(Section.WATERWORKS, 40, "RF a machine can use per tick (machine with first capacitor upgrade)", true, false), //
  waterWorksRFusePerTick3(Section.WATERWORKS, 60, "RF a machine can use per tick (machine with second capacitor upgrade)", true, false), //

  waterWorksRFinPerTick1(Section.WATERWORKS, 100, "RF a machine can accept per tick (machine with no capacitor upgrades)", true, false), //
  waterWorksRFinPerTick2(Section.WATERWORKS, 200, "RF a machine can accept per tick (machine with first capacitor upgrade)", true, false), //
  waterWorksRFinPerTick3(Section.WATERWORKS, 800, "RF a machine can accept per tick (machine with second capacitor upgrade)", true, false), //

  waterWorksEnabled(Section.RECIPES, false, "Enable the crafting recipe for the Waterworks", true, true), //

  // defaults give exactly 20/40/100 RF/t. (16+6*64=400 per operation, divided by 20/10/4)
  impulseHopperRFusePerOperation(Section.IHOPPER, 16, "RF use per operation (regardless of its outcome)"), //
  impulseHopperRFusePerItem(Section.IHOPPER, 1.0d, "RF use per transfered item"), //

  impulseHopperWorkEveryTick1(Section.IHOPPER, 20, "How many ticks does it take to execute one operation (machine with no capacitor upgrades)"), //
  impulseHopperWorkEveryTick2(Section.IHOPPER, 10, "How many ticks does it take to execute one operation (machine with first capacitor upgrade)"), //
  impulseHopperWorkEveryTick3(Section.IHOPPER, 4, "How many ticks does it take to execute one operation (machine with second capacitor upgrade)"), //

  impulseHopperEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Impulse Hopper", true, true), //

  niardContinuousEnergyUseRF(Section.NIARD, 10, "The amount of power used by a niard per tick.", true, false), // 
  niardPerBucketEnergyUseRF(Section.NIARD, 400, "The amount of power used by a niard per 1000mB of liquid placed into the world."), //

  niardAllowWaterInHell(Section.NIARD, false, "Is the Niard allowed to place water in the Nether?"), //

  niardEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Niard", true, true), //

  voidTankContinuousEnergyUseRF1(Section.VOIDTANK, 10, "The amount of power used by a Portal Tank per tick (machine with no capacitor upgrades)", true, false), // 
  voidTankContinuousEnergyUseRF2(Section.VOIDTANK, 100, "The amount of power used by a Portal Tank per tick (machine with first capacitor upgrade)", true,
      false), // 
  voidTankContinuousEnergyUseRF3(Section.VOIDTANK, 10000, "The amount of power used by a Portal Tank per tick (machine with second capacitor upgrade)", true,
      false), // 

  voidTankEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Portal Tank", true, true), //

  pMonEnableDynamicTextures(Section.PMON, true, "Enable dynamic texture updating for the Graphical Power Monitor. Disabling this also reduces network trafic"), //

  pMonEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Graphical Power Monitor", true, true), //

  tcomRecyclingLossFix(Section.TCOM, 0.25d, "Fix loss of recycling in the Loot Recycler (0-1)", true, false), //
  tcomRecyclingLossChanced(Section.TCOM, 0.5d, "Chance based loss of recycling in the Loot Recycler (0-1)", true, false), //

  tcomRecyclingPowerPerTick(Section.TCOM, 10, "RF/tick for recycling in the Loot Recycler", true, false), //
  tcomRecyclingTicksPerItem(Section.TCOM, 45, "Ticks for recycling one item in the Loot Recycler (Note: Sound plays for 45 ticks)"), //

  tcomExtractingPowerPerItem(Section.TCOM, 10, "RF for extracting one recycled item from the Loot Recycler"), //

  tcomEnchantingPowerPerTick(Section.TCOM, 125, "RF/tick for enchanting in the Loot Recycler", true, false), //
  tcomEnchantingTicksPerItem(Section.TCOM, 80, "Ticks for enchanting one item in the Loot Recycler"), //
  tcomEnchantingPowerPerFailure(Section.TCOM, 1250, "RF wasted on failing to enchant in the Loot Recycler"), //

  tcomPowerIntakePerTick(Section.TCOM, 100, "RF/tick the Loot Recycler can take", true, false), //

  tcomEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Loot Recycler", true, true), //

  magcMagnetizingPowerPerTick(Section.MAGCHARGER, 10, "RF/tick for magnetizing in the Magnetic Charger", true, false), //
  magcMagnetizingTicksPerItem(Section.MAGCHARGER, 20, "Ticks for magnetizing one item in the Magnetic Charger (Note: Sound plays for 20 ticks)"), //
  magcPowerIntakePerTick(Section.MAGCHARGER, 100, "RF/tick the Magnetic Charger can take", true, false), //

  magcEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Magnetic Charger", true, true), //

  flagDemagnetizingChance(Section.FLAG, 0.04d, "Chance that a Magnetic Flag demagnetizes when broken (0-1)"), //
  flagKeepTargetOnBreaking(Section.FLAG, false, "Should Magnetic Flags keep their target when being broken?"), //

  flagEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Magnetic Flag", true, true), //

  decoBlockInvisibleIsInvisible(Section.DECO, true, "Allow the invisible block to be invisible. Otherwise it will mimic a quite clear glass block.", true,
      false), //

  decoBlockEnabled(Section.RECIPES, true, "Enable the crafting recipe for the decoration block", true, true), //

  farmNO_POWEREnabled(Section.FARM_NOTIF, true, "Enable the notification NO_POWER"), //
  farmNO_HOEEnabled(Section.FARM_NOTIF, true, "Enable the notification NO_HOE"), //
  farmNO_RAKEEnabled(Section.FARM_NOTIF, true, "Enable the notification NO_RAKE"), //
  farmNO_CROPSEnabled(Section.FARM_NOTIF, true, "Enable the notification NO_CROPS"), //
  farmNO_SEEDSEnabled(Section.FARM_NOTIF, true, "Enable the notification NO_SEEDS"), //
  farmNO_FERTILIZEREnabled(Section.FARM_NOTIF, false, "Enable the notification NO_FERTILIZER"), //
  farmFULLEnabled(Section.FARM_NOTIF, true, "Enable the notification FULL"), //
  farmWEEDSEnabled(Section.FARM_NOTIF, true, "Enable the notification WEEDS"), //
  farmOFFLINEEnabled(Section.FARM_NOTIF, true, "Enable the notification OFFLINE"), //

  ghostItemsEnabled(Section.VISUALS, true, "Enable item ghosts in the GUI"), //
  directDrawingEnabled(Section.VISUALS, true,
      "Allow openGL drawing without tesselator. Disable this if certain machines don't render. Be prepared for texture mapping errors when this is off."), //
  rotationInItemframesEnabled(Section.VISUALS, true, "Blocks rotate when placed in an item frame"), //

  farmBreaksWoodenRakesEnabled(Section.FARM, true, "If enabled, the farming station can break wooden hand rakes"), //
  farmBreaksIronRakesEnabled(Section.FARM, true, "If enabled, the farming station can break iron hand rakes"), //
  farmBreaksWoodenRakesChance(Section.FARM, 1d / 300d, "Chance that a farming station breaks a wooden hand rake (0-1)"), //
  farmBreaksIronRakesChance(Section.FARM, 1d / 900d, "Chance that a farming station breaks a wooden hand rake (0-1)"), //

  farmRFperTickPerArea(Section.FARM, 2.5d, "RF used per tick per 'ring' of farmland", true, false), //
  farmRFperTickPerModule(Section.FARM, 5d, "RF used per tick per installed module", true, false), //

  farmRFperAnalyzing(Section.FARM, 1000, "RF used when analyzing a seed"), //
  farmRFperCrops(Section.FARM, 100, "RF used when placing down crop sticks"), //
  farmRFperCrossCrops(Section.FARM, 100, "RF used when placing down cross crop sticks"), //
  farmRFperFertilize(Section.FARM, 750, "RF used when fertilizing a plant"), //
  farmRFperHarvest(Section.FARM, 200, "RF used when harvest produce"), //
  farmRFperPlant(Section.FARM, 2000, "RF used when planting a seed"), //
  farmRFperRaking(Section.FARM, 100, "RF used when raking with a hand rake"), //
  farmRFperRakingIndustrial(Section.FARM, 200, "RF used when raking with an industrial rake"), //
  farmRFperSeedHarvest(Section.FARM, 500, "RF used when removing a plant to get back seeds, crop sticks and potentially harvest"), //
  farmRFperTill(Section.FARM, 2000, "RF used when tilling a block"), //

  /*
   * The default values will give fields of 48/120/360 blocks. It will take
   * 960/1200/1080 ticks or 48/60/54 seconds per field (assuming there are no
   * double actions).
   */

  farmArea1(Section.FARM, 3, "'Rings' of farmland a farm with no capacitor upgrades will work", true, false), //
  farmArea2(Section.FARM, 5, "'Rings' of farmland a farm with first capacitor upgrade will work", true, false), //
  farmArea3(Section.FARM, 9, "'Rings' of farmland a farm with second capacitor upgrade will work", true, false), //

  farmDelay1(Section.FARM, 20, "number of ticks it takes a farm with no capacitor upgrades to work one tile of land"), //
  farmDelay2(Section.FARM, 10, "number of ticks it takes a farm with first capacitor upgrade to work one tile of land"), //
  farmDelay3(Section.FARM, 3, "number of ticks it takes a farm with second capacitor upgrade to work one tile of land"), //

  seedsRFperGrowthTick(Section.FARM, 100000, "RF it takes the Capacitor Plant to try to grow"), //
  seedsTierEasy(Section.FARM, 3, "tier of the Capacitor seeds when 'easy recipes' are selected"), //
  seedsTierHard(Section.FARM, 5, "tier of the Capacitor seeds when 'hard recipes' are selected"), //

  farmSeedlessRecipesEnabled(Section.FARM, false,
      "If enabled there will be recipes for the farm that don't require you to gro capacitor plants. I call it cheat mode"), //
  seedsAutomaticHarvestingEnabled(Section.FARM, false, "If enabled Capacitor Plants can be harvested with machines. I call it couch potato mode"), //
  seedsBonemealEnabled(Section.FARM, false, "If enabled Capacitor Plants can be fertilized with bonemeal."), //
  farmAggressiveTillingDefault(Section.FARM, true, "The initial value of 'till agressively' for newly placed farms"), //

  farmDebugLoggingEnabled(Section.FARM, false, "When enabled, the farm will write extensive information about its operation into the logfile"), //

  farmEnabled(Section.RECIPES, true, "Enable the crafting recipe for the AgriCraft farming station (will only work if AgriCraft is installed)", true, true), //

  flowingMilkCuresPoison(Section.LIQUIDS, 0.001, "Chance that flowing milk will cure poison (applies once per tick)"), //
  sourceMilkIsConsumedWhenCuringPoison(Section.LIQUIDS, 0.25, "Chance that a source block of milk will be consumed when curing poison"), //

  fortuneCookiesEnabled(Section.RECIPES, true, "When enabled, the fortune cookies will be visible ingame"), //
  fortuneCookiesCraftingEnabled(Section.RECIPES, true, "When enabled, the fortune cookies will have a crafting recipe", true, true), //

  leversEnabled(Section.RECIPES, "10,30,60,300",
      "A comma-seperated list of durations in seconds. For these, self-reseting levers will be created. Set to 0 to disable the lever", true, true), //

  profilingEnabled(Section.DEV, false, "When enabled, profiling information will be written into the logfile"), //
  ;

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Nothing to see beyond this point. End of configuration values.
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Nonnull
  private final Section section;
  @Nonnull
  private final Object defaultValue;
  @Nonnull
  private final String description;
  @Nonnull
  private Object currentValue;
  private final boolean requiresWorldRestart;
  private final boolean requiresGameRestart;

  private Config(@Nonnull Section section, @Nonnull Object defaultValue, @Nonnull String description, boolean requiresWorldRestart, boolean requiresGameRestart) {
    this.section = section;
    this.description = description;
    this.currentValue = this.defaultValue = defaultValue;
    this.requiresWorldRestart = requiresWorldRestart;
    this.requiresGameRestart = requiresGameRestart;
  }

  private Config(@Nonnull Section section, @Nonnull Integer defaultValue, @Nonnull String description, boolean requiresWorldRestart, boolean requiresGameRestart) {
    this(section, (Object) defaultValue, description, requiresWorldRestart, requiresGameRestart);
  }

  private Config(@Nonnull Section section, @Nonnull Double defaultValue, @Nonnull String description, boolean requiresWorldRestart, boolean requiresGameRestart) {
    this(section, (Object) defaultValue, description, requiresWorldRestart, requiresGameRestart);
  }

  private Config(@Nonnull Section section, @Nonnull Boolean defaultValue, @Nonnull String description, boolean requiresWorldRestart, boolean requiresGameRestart) {
    this(section, (Object) defaultValue, description, requiresWorldRestart, requiresGameRestart);
  }

  private Config(@Nonnull Section section, @Nonnull String defaultValue, @Nonnull String description, boolean requiresWorldRestart, boolean requiresGameRestart) {
    this(section, (Object) defaultValue, description, requiresWorldRestart, requiresGameRestart);
  }

  private Config(@Nonnull Section section, @Nonnull Integer defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description, false, false);
	  }

  private Config(@Nonnull Section section, @Nonnull Double defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description, false, false);
  }

  private Config(@Nonnull Section section, @Nonnull Boolean defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description, false, false);
  }

  private Config(@Nonnull Section section, @Nonnull String defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description, false, false);
  }

  void load(Configuration config) {
    Object value = null;
    if (defaultValue instanceof Integer) {
      value = setPropertyData(config.get(section.name, name(), (Integer) defaultValue, description)).getInt((Integer) defaultValue);
    } else if (defaultValue instanceof Double) {
      value = setPropertyData(config.get(section.name, name(), (Double) defaultValue, description)).getDouble((Double) defaultValue);
    } else if (defaultValue instanceof Boolean) {
      value = setPropertyData(config.get(section.name, name(), (Boolean) defaultValue, description)).getBoolean((Boolean) defaultValue);
    } else if (defaultValue instanceof String) {
      value = setPropertyData(config.get(section.name, name(), (String) defaultValue, description)).getString();
    }

    setField(value);
  }

  private Property setPropertyData(final Property property) {
    property.setRequiresWorldRestart(requiresWorldRestart);
    property.setRequiresMcRestart(requiresGameRestart);
    return property;
  }

  private void setField(Object value) {
    if (value != null) {
      currentValue = value;
    }
  }

  void store(ByteBuf buf) {
    if (defaultValue instanceof Integer) {
      buf.writeInt(getInt());
    } else if (defaultValue instanceof Double) {
      buf.writeDouble(getDouble());
    } else if (defaultValue instanceof Boolean) {
      buf.writeBoolean(getBoolean());
    } else if (defaultValue instanceof String) {
      String value = getString();
      byte[] bytes = value.getBytes(Charset.forName("UTF-8"));
      buf.writeInt(bytes.length);
      buf.writeBytes(bytes);
    }
  }

  void read(ByteBuf buf) {
    Object value = null;
    if (defaultValue instanceof Integer) {
      value = buf.readInt();
    } else if (defaultValue instanceof Double) {
      value = buf.readDouble();
    } else if (defaultValue instanceof Boolean) {
      value = buf.readBoolean();
    } else if (defaultValue instanceof String) {
      int len = buf.readInt();
      byte[] bytes = new byte[len];
      buf.readBytes(bytes, 0, len);
      value = new String(bytes, Charset.forName("UTF-8"));
    }
    setField(value);
  }

  protected void resetToDefault() {
    setField(defaultValue);
  }

  public Section getSection() {
    return section;
  }

  //

  private class DataTypeErrorInConfigException extends RuntimeException {
  }

  public int getDefaultInt() {
    if (defaultValue instanceof Integer) {
      return (Integer) defaultValue;
    } else if (defaultValue instanceof Double) {
      return ((Double) defaultValue).intValue();
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) defaultValue ? 1 : 0;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  public double getDefaultDouble() {
    if (defaultValue instanceof Integer) {
      return (Integer) defaultValue;
    } else if (defaultValue instanceof Double) {
      return (Double) defaultValue;
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) defaultValue ? 1 : 0;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  public float getDefaultFloat() {
    if (defaultValue instanceof Integer) {
      return (Integer) defaultValue;
    } else if (defaultValue instanceof Double) {
      return ((Double) defaultValue).floatValue();
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) defaultValue ? 1 : 0;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  public boolean getDefaultBoolean() {
    if (defaultValue instanceof Integer) {
      throw new DataTypeErrorInConfigException();
    } else if (defaultValue instanceof Double) {
      throw new DataTypeErrorInConfigException();
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) defaultValue;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  @SuppressWarnings("null")
  @Nonnull
  public String getDefaultString() {
    if (defaultValue instanceof Integer) {
      return ((Integer) defaultValue).toString();
    } else if (defaultValue instanceof Double) {
      return ((Double) defaultValue).toString();
    } else if (defaultValue instanceof Boolean) {
      return ((Boolean) defaultValue).toString();
    } else if (defaultValue instanceof String) {
      return (String) defaultValue;
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  //

  public int getInt() {
    if (defaultValue instanceof Integer) {
      return (Integer) currentValue;
    } else if (defaultValue instanceof Double) {
      return ((Double) currentValue).intValue();
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) currentValue ? 1 : 0;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  public double getDouble() {
    if (defaultValue instanceof Integer) {
      return (Integer) currentValue;
    } else if (defaultValue instanceof Double) {
      return (Double) currentValue;
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) currentValue ? 1 : 0;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  public float getFloat() {
    if (defaultValue instanceof Integer) {
      return (Integer) currentValue;
    } else if (defaultValue instanceof Double) {
      return ((Double) currentValue).floatValue();
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) currentValue ? 1 : 0;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  public boolean getBoolean() {
    if (defaultValue instanceof Integer) {
      throw new DataTypeErrorInConfigException();
    } else if (defaultValue instanceof Double) {
      throw new DataTypeErrorInConfigException();
    } else if (defaultValue instanceof Boolean) {
      return (Boolean) currentValue;
    } else if (defaultValue instanceof String) {
      throw new DataTypeErrorInConfigException();
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }

  @SuppressWarnings("null")
  @Nonnull
  public String getString() {
    if (defaultValue instanceof Integer) {
      return ((Integer) currentValue).toString();
    } else if (defaultValue instanceof Double) {
      return ((Double) currentValue).toString();
    } else if (defaultValue instanceof Boolean) {
      return ((Boolean) currentValue).toString();
    } else if (defaultValue instanceof String) {
      return (String) currentValue;
    } else {
      throw new DataTypeErrorInConfigException();
    }
  }
}
