package info.loenwind.enderioaddons.machine.waterworks.engine;

import java.util.ArrayList;
import java.util.List;



public class Material implements Comparable<Material> {
  private final String name;
  private final int prio;
  private final Item item;
  private final List<Component> components = new ArrayList<Component>();
  private final Double volume;
  private final Double density;
  
  public Material(String name, int prio, Item item, Double volume, Double density) {
    this.name = name;
    this.prio = prio;
    this.item = item;
    this.volume = volume;
    this.density = density;
  }

  public String getName() {
    return name;
  }

  public int getPrio() {
    return prio;
  }

  public Item getItem() {
    return item;
  }

  public List<Component> getComponents() {
    return components;
  }

  public Double getVolume() {
    return volume;
  }

  public Double getDensity() {
    return density;
  }

  @Override
  public int compareTo(Material o) {
    return Integer.compare(prio, o.prio);
  }
}
