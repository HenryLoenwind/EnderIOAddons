package info.loenwind.enderioaddons.config;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;

public enum ConfigValues {

  drainContinuousEnergyUseRF(Section.DRAIN, 10, "The amount of power used by a drain per tick."), // 
  drainPerBucketEnergyUseRF(Section.DRAIN, 400, "The amount of power used by a drain per 1000mB of liquid collected."), //
  drainPerSourceBlockMoveEnergyUseRF(Section.DRAIN, 250,
      "The amount of power used by a drain to move a source block by one meter."), //
  drainAllowOnDedicatedServer(Section.DRAIN, false, "Allow the use of the drain on a dedicated server."), //
  drainEnabled(Section.DRAIN, "drainEnabled", true, "Enables/disables the drain's crafting recipe."), //

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
  ;

  @Nonnull
  private final Section section;
  @Nonnull
  private final String name;
  @Nonnull
  private final Object defaultValue;
  @Nonnull
  private final String description;

  private ConfigValues(@Nonnull Section section, @Nullable String name, @Nonnull Object defaultValue, @Nonnull String description) {
    this.section = section;
    this.name = name == null ? this.name() : name;
    this.description = description;
    this.defaultValue = defaultValue;
  }

  private ConfigValues(@Nonnull Section section, @Nonnull String name, @Nonnull Integer defaultValue, @Nonnull String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(@Nonnull Section section, @Nonnull String name, @Nonnull Double defaultValue, @Nonnull String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(@Nonnull Section section, @Nonnull String name, @Nonnull Boolean defaultValue, @Nonnull String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(@Nonnull Section section, @Nonnull String name, @Nonnull String defaultValue, @Nonnull String description) {
    this(section, name, (Object) defaultValue, description);
  }

  private ConfigValues(@Nonnull Section section, @Nonnull Integer defaultValue, @Nonnull String description) {
	    this(section, null, (Object) defaultValue, description);
	  }

  private ConfigValues(@Nonnull Section section, @Nonnull Double defaultValue, @Nonnull String description) {
    this(section, null, (Object) defaultValue, description);
  }

  private ConfigValues(@Nonnull Section section, @Nonnull Boolean defaultValue, @Nonnull String description) {
    this(section, null, (Object) defaultValue, description);
  }

  private ConfigValues(@Nonnull Section section, @Nonnull String defaultValue, @Nonnull String description) {
    this(section, null, (Object) defaultValue, description);
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

    setField(value);
  }

  private void setField(Object value) {
    try {
      Field field = Config.class.getDeclaredField(name);
      field.set(null, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void toBytes(ByteBuf buf) {
    for (ConfigValues value : values()) {
      if (value.section.sync) {
        value.store(buf);
      }
    }
  }

  private void store(ByteBuf buf) {
    try {
      Field field = Config.class.getDeclaredField(name);
      if (defaultValue instanceof Integer) {
        buf.writeInt(field.getInt(null));
      } else if (defaultValue instanceof Double) {
        buf.writeDouble(field.getDouble(null));
      } else if (defaultValue instanceof Boolean) {
        buf.writeBoolean(field.getBoolean(null));
      } else if (defaultValue instanceof String) {
        String value = (String) field.get(null);
        byte[] bytes = value.getBytes(Charset.forName("UTF-8"));
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void fromBytes(ByteBuf buf) {
    for (ConfigValues value : values()) {
      if (value.section.sync) {
        value.read(buf);
      }
    }
  }

  private void read(ByteBuf buf) {
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

  public static void loadAll(Configuration config) {
    for (ConfigValues value : values()) {
      value.load(config);
    }
  }

  public static void loadAll() {
    for (ConfigValues value : values()) {
      value.setField(value.defaultValue);
    }
  }

  @Nonnull
  public Integer getDefaultInt() {
    return (Integer) defaultValue;
  }

  @Nonnull
  public Double getDefaultDouble() {
    return (Double) defaultValue;
  }

  @Nonnull
  public Boolean getDefaultBoolean() {
    return (Boolean) defaultValue;
  }

  @Nonnull
  public String getDefaultString() {
    return (String) defaultValue;
  }

}
