package info.loenwind.enderioaddons.machine.waterworks.engine;

import javax.annotation.Nonnull;

public class Component {
  @Nonnull
  private final String name;
  private final double granularity;
  private final double factor;
  private final int count;

  public Component(@Nonnull String name, double granularity, double factor, int count) {
    this.name = name;
    this.granularity = granularity;
    this.factor = factor;
    this.count = count;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public double getGranularity() {
    return granularity;
  }

  public double getFactor() {
    return factor;
  }

  public int getCount() {
    return count;
  }

}