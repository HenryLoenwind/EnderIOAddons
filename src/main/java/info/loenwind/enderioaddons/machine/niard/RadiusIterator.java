package info.loenwind.enderioaddons.machine.niard;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.common.util.BlockCoord;

public class RadiusIterator {

  private final List<BlockCoord> bcl = new ArrayList<>();
  private int idx = -1;

  public RadiusIterator(BlockCoord bc, int radius) {
    bcl.add(bc);
    for (int i = 1; i <= radius; i++) {
      for (int j = -i; j < i; j++) {
        bcl.add(new BlockCoord(bc.x - i, bc.y, bc.z + j));
        bcl.add(new BlockCoord(bc.x + i, bc.y, bc.z - j));
        bcl.add(new BlockCoord(bc.x + j, bc.y, bc.z + i));
        bcl.add(new BlockCoord(bc.x - j, bc.y, bc.z - i));
      }
    }
  }

  public BlockCoord next() {
    if (++idx >= bcl.size()) {
      idx = 0;
    }
    return bcl.get(idx);
  }

  public int size() {
    return bcl.size();
  }

}
