package info.loenwind.enderioaddons.machine.ihopper;

import java.util.List;
import java.util.Locale;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;

public enum RedstoneMode implements ICycleEnum {

  IGNORE(IconEIO.REDSTONE_MODE_ALWAYS), ON(IconEIO.REDSTONE_MODE_WITH_SIGNAL), OFF(IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL), NEVER(IconEIO.REDSTONE_MODE_NEVER), RAISING_EDGE(
      null), FALLING_EDGE(null);

  private IWidgetIcon icon;

  RedstoneMode(IWidgetIcon icon) {
    this.icon = icon;
  }

  public String getTooltip() {
    return EnderIO.lang.localize("gui.tooltip.redstoneControlMode." + name().toLowerCase(Locale.US));
  }

  @Override
  public IWidgetIcon getIcon() {
    return icon;
  }

  @Override
  public List<String> getTooltipLines() {
    return Lists.newArrayList(getTooltip());
  }

  public boolean isConditionMet(int powerLevel, int prevPowerLevel) {
    switch (this) {
    case IGNORE:
      return true;
    case NEVER:
      return false;
    case OFF:
      return powerLevel < 1;
    case ON:
      return powerLevel > 0;
    case RAISING_EDGE:
      return prevPowerLevel < 1 && powerLevel > 0;
    case FALLING_EDGE:
      return prevPowerLevel > 0 && powerLevel < 1;
    }
    throw new RuntimeException("enum has unexpected values");
  }

  //te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord));

  public RedstoneMode next() {
    int ord = ordinal();
    if (ord == values().length - 1) {
      ord = 0;
    } else {
      ord++;
    }
    return values()[ord];
  }

  public RedstoneMode previous() {
    int ord = ordinal();
    ord--;
    if (ord < 0) {
      ord = values().length - 1;
    }
    return values()[ord];
  }

}
