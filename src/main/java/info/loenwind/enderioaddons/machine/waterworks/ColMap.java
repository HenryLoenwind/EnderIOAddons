package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.common.Fluids;
import net.minecraftforge.fluids.Fluid;

public class ColMap {

  private static final int COL_INPUT_FLUID = 0;
  private static final int COL_INPUT_FLUID_ID = 1;
  private static final int COL_OUTPUT_FLUID = 2;
  private static final int COL_OUTPUT_FLUID_ID = 3;
  private static final int COL_LEVEL = 4;
  private static final int COL_OUTPUT_AMOUNT = 5;

  private final Object[] x;
  private final int cols = 6;
  private final int elements;

  public ColMap(int elements) {
    x = new Object[elements * cols];
    this.elements = elements;
  }

  private Object get(int col, int elem) {
    return x[elem * cols + col];
  }

  private void set(int col, int elem, Object o) {
    x[elem * cols + col] = o;
  }

  private boolean contains(int col, Object o) {
    for (int i = 0; i <= elements; i++) {
      Object c = get(col, i);
      if (o == null && c == null || (o != null && o.equals(c))) {
        return true;
      }
    }
    return false;
  }

  private int find(int col, Object o) {
    for (int i = 0; i <= elements; i++) {
      Object c = get(col, i);
      if (o == null && c == null || (o != null && o.equals(c))) {
        return i;
      }
    }
    return -1;
  }

  public boolean containsInputLiquid(Fluid l) {
    return contains(COL_INPUT_FLUID_ID, l.getID());
  }

  public boolean isMatchingInputForOutput(Fluid input, Fluid output) {
    if (input == null || output == null) {
      return false;
    }
    int elem = find(COL_OUTPUT_FLUID_ID, output.getID());
    if (elem < 0) {
      return false;
    }
    return get(COL_INPUT_FLUID_ID, elem).equals(input.getID());
  }

  public boolean isMatchingOutputForInput(Fluid input, Fluid output) {
    if (input == null) {
      return false;
    }
    if (output == null) {
      return true;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return false;
    }
    Object expectedOutput = get(COL_OUTPUT_FLUID_ID, elem);
    return expectedOutput != null && expectedOutput.equals(output.getID());
  }

  public int getLevelFromInput(Fluid input) {
    if (input == null) {
      return -1;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return -1;
    }
    return (Integer) get(COL_LEVEL, elem);
  }

  public int getOutputAmountFromInput(Fluid input) {
    if (input == null) {
      return -1;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return -1;
    }
    return (Integer) get(COL_OUTPUT_AMOUNT, elem);
  }

  public Fluid getOutputFromInput(Fluid input) {
    if (input == null) {
      return null;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return null;
    }
    return (Fluid) get(COL_OUTPUT_FLUID, elem);
  }

  public void set(int elem, Fluid input, Fluids output, int level, int amount) {
    set(COL_INPUT_FLUID, elem, input);
    set(COL_INPUT_FLUID_ID, elem, input.getID());
    set(COL_OUTPUT_FLUID, elem, output.getFluid());
    set(COL_OUTPUT_FLUID_ID, elem, output.getFluid().getID());
    set(COL_LEVEL, elem, level);
    set(COL_OUTPUT_AMOUNT, elem, amount);
  }

  public void set(int elem, Fluids input, Fluids output, int level, int amount) {
    set(COL_INPUT_FLUID, elem, input.getFluid());
    set(COL_INPUT_FLUID_ID, elem, input.getFluid().getID());
    set(COL_OUTPUT_FLUID, elem, output.getFluid());
    set(COL_OUTPUT_FLUID_ID, elem, output.getFluid().getID());
    set(COL_LEVEL, elem, level);
    set(COL_OUTPUT_AMOUNT, elem, amount);
  }

  public void set(int elem, Fluids input, int level) {
    set(COL_INPUT_FLUID, elem, input.getFluid());
    set(COL_INPUT_FLUID_ID, elem, input.getFluid().getID());
    set(COL_OUTPUT_FLUID, elem, null);
    set(COL_OUTPUT_FLUID_ID, elem, null);
    set(COL_LEVEL, elem, level);
    set(COL_OUTPUT_AMOUNT, elem, 0);
  }

}
