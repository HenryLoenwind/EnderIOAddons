package info.loenwind.enderioaddons.config;

import java.lang.reflect.Field;

import net.minecraftforge.common.config.Configuration;

public enum ConfigValues {

  drainContinuousEnergyUseRF(Section.DRAIN, "drainContinuousEnergyUseRF", 10, "The amount of power used by a drain per tick."), // 
  drainPerBucketEnergyUseRF(Section.DRAIN, "drainPerBucketEnergyUseRF", 400, "The amount of power used by a drain per 1000mB of liquid collected."), //
  drainPerSourceBlockMoveEnergyUseRF(Section.DRAIN, "drainPerSourceBlockMoveEnergyUseRF", 250,
      "The amount of power used by a drain to move a source block by one meter."), //
  drainAllowOnDedicatedServer(Section.DRAIN, "drainAllowOnDedicatedServer", false, "Allow the use of the drain on a dedicated server."), //
  drainEnabled(Section.DRAIN, "drainEnabled", true, "Enables/disables the drain's crafting recipe."), //

  cobbleWorksRfPerCobblestone(Section.COBBLEWORKS, "cobbleWorksRfPerCobblestone", 100, "RF used per generated cobblestone"), //

  cobbleWorksRfDiscountForCrafting(Section.COBBLEWORKS, "cobbleWorksRfDiscountForCrafting", 10,
      "Discount on the RF cost of crafting operations in percent (0-100)"), //
  cobbleWorksRfDiscountForSmelting(Section.COBBLEWORKS, "cobbleWorksRfDiscountForSmelting", 10,
      "Discount on the RF cost of smelting operations in percent (0-100)"), //
  cobbleWorksRfDiscountForCrushing(Section.COBBLEWORKS, "cobbleWorksRfDiscountForCrushing", 10,
      "Discount on the RF cost of crusing (sagmilling) operations in percent (0-100)"), //
  cobbleWorksRfDiscountPerUpgrade(Section.COBBLEWORKS, "cobbleWorksRfDiscountPerUpgrade", 10,
      "Discount on the RF cost of any operation in percent (0-100). This discount is applied once for a double-layer capacitor "
          + "and twice for an octadic capacitor upgrade.");

  private final Section section;
  private final String name;
  private final Object defaultValue;
  private final String description;

  private ConfigValues(Section section, String name, Object defaultValue, String description) {
    this.section = section;
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
  }

  private ConfigValues(Section section, String name, Integer defaultValue, String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(Section section, String name, Double defaultValue, String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(Section section, String name, Boolean defaultValue, String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(Section section, String name, String defaultValue, String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private void load(Configuration config) {
    Object value = null;
    if (defaultValue instanceof Integer) {
      value = config.get(section.name, name, (Integer) defaultValue, description).getInt((Integer) defaultValue);
    } else if (defaultValue instanceof Double) {
      value = config.get(section.name, name, (Double) defaultValue, description).getDouble((Double) defaultValue);
    } else if (defaultValue instanceof Boolean) {
      value = config.get(section.name, name, (Boolean) defaultValue, description).getBoolean((Boolean) defaultValue);
    } else if (defaultValue instanceof String) {
      value = config.get(section.name, name, (String) defaultValue, description).getString();
    }

    try {
      Field field = Config.class.getDeclaredField(name);
      field.set(null, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void loadAll(Configuration config) {
    for (ConfigValues value : values()) {
      value.load(config);
    }
  }

  public Integer getDefaultInt() {
    return (Integer) defaultValue;
  }

  public Double getDefaultDouble() {
    return (Double) defaultValue;
  }

  public Boolean getDefaultBoolean() {
    return (Boolean) defaultValue;
  }

  public String getDefaultString() {
    return (String) defaultValue;
  }

}
