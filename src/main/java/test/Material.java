package test;

import java.util.List;


public class Material implements Comparable<Material> {
  String name;
  int prio;
  Item item;
  List<Component> components;
  Double volume;
  Double density;
  
  @Override
  public int compareTo(Material o) {
    return Integer.compare(prio, o.prio);
  }
}
