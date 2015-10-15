package info.loenwind.enderioaddons.machine.pmon;

public class StatCollector {

  public static final int MAX_VALUES = StatArray.MAX_VALUES;
  private final StatArray mins = new StatArray();
  private final StatArray maxs = new StatArray();
  private final int collectTarget;
  private int collectCount;
  private int pos;

  public StatCollector(int collectTarget) {
    this.collectTarget = collectTarget;
    this.collectCount = collectTarget;
    this.pos = -1;
  }

  public void addValue(int value) {
    assert collectCount <= collectTarget;
    assert collectCount > 0;
    assert pos < MAX_VALUES;
    if (collectCount == collectTarget) {
      pos++;
      assert pos >= 0;
      if (pos >= MAX_VALUES) {
        pos = 0;
      }
      mins.setValue(pos, value);
      maxs.setValue(pos, value);
      collectCount = 1;
    } else {
      assert pos >= 0;
      if (value < mins.getValue(pos)) {
        mins.setValue(pos, value);
      }
      if (value > maxs.getValue(pos)) {
        maxs.setValue(pos, value);
      }
      collectCount++;
    }
  }

  public int[][] getValues() {
    int[][] result = { new int[MAX_VALUES], new int[MAX_VALUES] };
    for (int i = 0; i < MAX_VALUES; i++) {
      int j = i + pos + 1;
      if (j >= MAX_VALUES) {
        j -= MAX_VALUES;
      }
      assert j >= 0;
      assert j < MAX_VALUES;
      result[0][i] = mins.getValue(j);
      result[1][i] = maxs.getValue(j);
    }
    return result;
  }

  public byte[] getData() {
    byte[] data = new byte[StatArray.BYTES * 2];
    mins.store(data, 0);
    maxs.store(data, StatArray.BYTES);
    return data;
  }

  public void setData(byte[] data) {
    if (data != null && data.length == StatArray.BYTES * 2) {
      mins.read(data, 0);
      maxs.read(data, StatArray.BYTES);
    }
  }

  public int getCollectCount() {
    return collectCount;
  }

  public void setCollectCount(int collectCount) {
    this.collectCount = collectCount;
  }

  public int getPos() {
    return pos;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

}
