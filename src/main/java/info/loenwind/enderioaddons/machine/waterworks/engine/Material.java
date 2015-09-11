package info.loenwind.enderioaddons.machine.waterworks.engine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class Material implements Comparable<Material> {
  @Nonnull
  private final String name;
  private final int prio;
  @Nonnull
  private final Item item;
  @Nonnull
  private final List<Component> components = new ArrayList<Component>();
  private final double volume;
  private final double density;
  
  public Material(@Nonnull String name, int prio, @Nonnull Item item, double volume, double density) {
    this.name = name;
    this.prio = prio;
    this.item = item;
    this.volume = volume;
    this.density = density;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public int getPrio() {
    return prio;
  }

  @Nonnull
  public Item getItem() {
    return item;
  }

  @Nonnull
  public List<Component> getComponents() {
    return components;
  }

  public double getVolume() {
    return volume;
  }

  public double getDensity() {
    return density;
  }

  @Override
  public int compareTo(Material o) {
    return Integer.compare(prio, o.prio);
  }
}
