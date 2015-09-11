package info.loenwind.enderioaddons.machine.waterworks.engine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class Water extends Stash {
  @Nonnull
  private final List<Material> materials = new ArrayList<Material>();

  public Water() {
    super();
  }

  @Nonnull
  public List<Material> getMaterials() {
    return materials;
  }

}
