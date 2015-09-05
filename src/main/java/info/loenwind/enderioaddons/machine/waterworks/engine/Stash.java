package info.loenwind.enderioaddons.machine.waterworks.engine;

import java.util.HashMap;
import java.util.Map;

public class Stash {

  private final Map<String, Double> contents = new HashMap<String, Double>();

  public Stash() {
  }

  public Map<String, Double> getContents() {
    return contents;
  }

}