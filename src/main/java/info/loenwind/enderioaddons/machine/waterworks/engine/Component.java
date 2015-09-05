package info.loenwind.enderioaddons.machine.waterworks.engine;

public class Component {
  private final String name;
  private final Double granularity;
  private final Double factor;
  private final int count;

  public Component(String name, Double granularity, Double factor, int count) {
    this.name = name;
    this.granularity = granularity;
    this.factor = factor;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public Double getGranularity() {
    return granularity;
  }

  public Double getFactor() {
    return factor;
  }

  public int getCount() {
    return count;
  }

}