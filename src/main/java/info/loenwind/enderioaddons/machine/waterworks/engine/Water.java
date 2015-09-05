package info.loenwind.enderioaddons.machine.waterworks.engine;

import java.util.ArrayList;
import java.util.List;

public class Water extends Stash {
  private final List<Material> materials = new ArrayList<Material>();

  public Water() {
  }

  public List<Material> getMaterials() {
    return materials;
  }

}
