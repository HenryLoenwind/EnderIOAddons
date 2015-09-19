package info.loenwind.enderioaddons.config;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.minecraftforge.common.config.Configuration;

public enum Config {

  drainContinuousEnergyUseRF(Section.DRAIN, 10, "The amount of power used by a drain per tick."), // 
  drainPerBucketEnergyUseRF(Section.DRAIN, 400, "The amount of power used by a drain per 1000mB of liquid collected."), //
  drainPerSourceBlockMoveEnergyUseRF(Section.DRAIN, 250,
      "The amount of power used by a drain to move a source block by one meter."), //
  drainAllowOnDedicatedServer(Section.DRAIN, false, "Allow the use of the drain on a dedicated server."), //
  drainEnabled(Section.RECIPES, true, "Enable the crafting recipe for the drain"), //

  cobbleWorksRfPerCobblestone(Section.COBBLEWORKS, 100, "RF used per generated cobblestone"), //

  cobbleWorksRfDiscountForCrafting(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of crafting operations in percent (0-100)"), //
  cobbleWorksRfDiscountForSmelting(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of smelting operations in percent (0-100)"), //
  cobbleWorksRfDiscountForCrushing(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of crusing (sagmilling) operations in percent (0-100)"), //
  cobbleWorksRfDiscountPerUpgrade(Section.COBBLEWORKS, 10,
      "Discount on the RF cost of any operation in percent (0-100). This discount is applied once for a double-layer capacitor "
          + "and twice for an octadic capacitor upgrade."), //

  cobbleWorksEnabled(Section.RECIPES, true, "Enable the crafting recipe for the Cobbleworks"), //

  waterWorksWaterReductionPercentage(Section.WATERWORKS, 10, "Amount of liquid produced when processing in percent (0-100) of the input liquid"), //

  waterWorksRFperTask1(Section.WATERWORKS, 5 * 20 * 20 * 1.0d, "RF per task (machine with no capacitor upgrades)"), //
  waterWorksRFperTask2(Section.WATERWORKS, 5 * 20 * 20 * 1.75f * 0.9d, "RF per task (machine with first capacitor upgrades)"), //
  waterWorksRFperTask3(Section.WATERWORKS, 5 * 20 * 20 * 3 * 0.8d, "RF per task (machine with second capacitor upgrades)"), //

  waterWorksLiquidFactorperTask1(Section.WATERWORKS, 1.0d,
      "Amount of input liquid consumed per task as factor on the base value of 1000mB (machine with no capacitor upgrades)"), //
  waterWorksLiquidFactorperTask2(Section.WATERWORKS, 1.75d,
      "Amount of input liquid consumed per task as factor on the base value of 1000mB (machine with first capacitor upgrades)"), //
  waterWorksLiquidFactorperTask3(Section.WATERWORKS, 3.0d,
      "Amount of input liquid consumed per task as factor on the base value of 1000mB (machine with second capacitor upgrades)"), //

  waterWorksRFusePerTick1(Section.WATERWORKS, 20, "RF a machine can use per tick (machine with no capacitor upgrades)"), //
  waterWorksRFusePerTick2(Section.WATERWORKS, 40, "RF a machine can use per tick (machine with first capacitor upgrades)"), //
  waterWorksRFusePerTick3(Section.WATERWORKS, 60, "RF a machine can use per tick (machine with second capacitor upgrades)"), //

  waterWorksRFinPerTick1(Section.WATERWORKS, 100, "RF a machine can accept per tick (machine with no capacitor upgrades)"), //
  waterWorksRFinPerTick2(Section.WATERWORKS, 200, "RF a machine can accept per tick (machine with first capacitor upgrades)"), //
  waterWorksRFinPerTick3(Section.WATERWORKS, 800, "RF a machine can accept per tick (machine with second capacitor upgrades)"), //

  waterWorksEnabled(Section.RECIPES, false, "Enable the crafting recipe for the Waterworks"), //

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

  private Config(@Nonnull Section section, @Nonnull Object defaultValue, @Nonnull String description) {
    this.section = section;
    this.description = description;
    this.currentValue = this.defaultValue = defaultValue;
  }

  private Config(@Nonnull Section section, @Nonnull Integer defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description);
	  }

  private Config(@Nonnull Section section, @Nonnull Double defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description);
  }

  private Config(@Nonnull Section section, @Nonnull Boolean defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description);
  }

  private Config(@Nonnull Section section, @Nonnull String defaultValue, @Nonnull String description) {
    this(section, (Object) defaultValue, description);
  }

  void load(Configuration config) {
    Object value = null;
    if (defaultValue instanceof Integer) {
      value = config.get(section.name, name(), (Integer) defaultValue, description).getInt((Integer) defaultValue);
    } else if (defaultValue instanceof Double) {
      value = config.get(section.name, name(), (Double) defaultValue, description).getDouble((Double) defaultValue);
    } else if (defaultValue instanceof Boolean) {
      value = config.get(section.name, name(), (Boolean) defaultValue, description).getBoolean((Boolean) defaultValue);
    } else if (defaultValue instanceof String) {
      value = config.get(section.name, name(), (String) defaultValue, description).getString();
    }

    setField(value);
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
