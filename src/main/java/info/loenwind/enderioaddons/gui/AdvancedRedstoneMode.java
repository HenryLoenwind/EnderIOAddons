package info.loenwind.enderioaddons.gui;

import java.util.List;
import java.util.Locale;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;

public enum AdvancedRedstoneMode implements ICycleEnum {

  /**
   * Fire once when the power goes from LOW (0) to any HIGH value (1-15)
   */
  RISING_EDGE(IconEIOA.REDSTONE_MODE_RISING_EDGE), //
  /**
   * Fire once when the power goes from any HIGH value (1-15) to LOW (0)
   */
  FALLING_EDGE(IconEIOA.REDSTONE_MODE_FALLING_EDGE), //
  /**
   * Fire once when the power goes from LOW (0) to HIGH (14/15)
   */
  HIGH_PULSE(IconEIOA.REDSTONE_MODE_HIGH_PULSE), //
  /**
   * Fire once when the power goes from HIGH (14/15) to LOW (0)
   */
  LOW_PULSE(IconEIOA.REDSTONE_MODE_LOW_PULSE), //
  /**
   * Fire while the power is any HIGH value (1-15)
   */
  ON(IconEIOA.REDSTONE_MODE_ON_HIGH), //
  /**
   * Fire while the power is LOW (0)
   */
  OFF(IconEIOA.REDSTONE_MODE_ON_LOW), // 
  /**
   * Fire always
   */
  IGNORE(IconEIO.REDSTONE_MODE_ALWAYS), //
  /**
   * Fire never
   */
  NEVER(IconEIO.REDSTONE_MODE_NEVER), //
  ;

  private IWidgetIcon icon;

  AdvancedRedstoneMode(IWidgetIcon icon) {
    this.icon = icon;
  }

  public String getTooltip() {
    return EnderIO.lang.localizeExact("enderioaddons.gui.tooltip.redstoneControlMode." + name().toLowerCase(Locale.US));
  }

  @Override
  public IWidgetIcon getIcon() {
    return icon;
  }

  @Override
  public List<String> getTooltipLines() {
    return Lists.newArrayList(getTooltip());
  }

  public AdvancedRedstoneMode next() {
    int ord = ordinal();
    if (ord == values().length - 1) {
      ord = 0;
    } else {
      ord++;
    }
    return values()[ord];
  }

  public AdvancedRedstoneMode previous() {
    int ord = ordinal();
    ord--;
    if (ord < 0) {
      ord = values().length - 1;
    }
    return values()[ord];
  }

}
