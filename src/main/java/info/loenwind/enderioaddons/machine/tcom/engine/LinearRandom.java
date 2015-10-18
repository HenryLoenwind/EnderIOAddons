package info.loenwind.enderioaddons.machine.tcom.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LinearRandom {

  private LinearRandom() {
  }

  private static class Entry {
    final int maxV;
    final byte[] V;

    Entry(int lvl) {
      maxV = n(lvl);
      V = new byte[maxV];
      int k = 0;
      for (int i = 1; i <= lvl; i++) {
        for (int j = 0; j <= (lvl - i); j++) {
          V[k++] = (byte) i;
        }
      }
    }

    static int n(int lvl) {
      return lvl == 1 ? 1 : lvl + n(lvl - 1);
    }
  }

  private static final Map<Integer, Entry> Vec = new HashMap<>();

  private static void buildV(int lvl) {
    if (!Vec.containsKey(lvl)) {
      Vec.put(lvl, new Entry(lvl));
    }
  }

  public static int getValue(int maxLevel, Random rand) {
    if (maxLevel == 1) {
      return 1;
    }
    buildV(maxLevel);
    return Vec.get(maxLevel).V[rand.nextInt(Vec.get(maxLevel).maxV)];
  }
}
