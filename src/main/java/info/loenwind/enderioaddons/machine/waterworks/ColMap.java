package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.common.Fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.Fluid;

public class ColMap {

  private static final int COL_INPUT_FLUID = 0;
  private static final int COL_INPUT_FLUID_ID = 1;
  private static final int COL_OUTPUT_FLUID = 2;
  private static final int COL_OUTPUT_FLUID_ID = 3;
  private static final int COL_LEVEL = 4;
  private static final int COL_OUTPUT_AMOUNT = 5;

  @Nonnull
  private final Object[] x;
  private final int cols = 6;
  private final int elements;

  public ColMap(int elements) {
    x = new Object[elements * cols];
    this.elements = elements;
  }

  @Nullable
  private Object get(int col, int elem) {
    return x[elem * cols + col];
  }

  private void set(int col, int elem, @Nullable Object o) {
    x[elem * cols + col] = o;
  }

  private boolean contains(int col, @Nullable Object o) {
    for (int i = 0; i <= elements; i++) {
      Object c = get(col, i);
      if (o == null && c == null || (o != null && o.equals(c))) {
        return true;
      }
    }
    return false;
  }

  private int find(int col, @Nullable Object o) {
    for (int i = 0; i <= elements; i++) {
      Object c = get(col, i);
      if (o == null && c == null || (o != null && o.equals(c))) {
        return i;
      }
    }
    return -1;
  }

  public boolean containsInputLiquid(@Nonnull Fluid l) {
    return contains(COL_INPUT_FLUID_ID, l.getID());
  }

  public boolean isMatchingInputForOutput(@Nullable Fluid input, @Nullable Fluid output) {
    if (input == null || output == null) {
      return false;
    }
    int elem = find(COL_OUTPUT_FLUID_ID, output.getID());
    if (elem < 0) {
      return false;
    }
    final Object result = get(COL_INPUT_FLUID_ID, elem);
    return result != null && result.equals(input.getID());
  }

  public boolean isMatchingOutputForInput(@Nullable Fluid input, @Nullable Fluid output) {
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

  public int getLevelFromInput(@Nullable Fluid input) {
    if (input == null) {
      return -1;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return -1;
    }
    final Object result = get(COL_LEVEL, elem);
    return result != null ? (Integer) result : -1;
  }

  public int getOutputAmountFromInput(@Nullable Fluid input) {
    if (input == null) {
      return -1;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return -1;
    }
    final Object result = get(COL_OUTPUT_AMOUNT, elem);
    return result != null ? (Integer) result : -1;
  }

  public Fluid getOutputFromInput(@Nullable Fluid input) {
    if (input == null) {
      return null;
    }
    int elem = find(COL_INPUT_FLUID_ID, input.getID());
    if (elem < 0) {
      return null;
    }
    return (Fluid) get(COL_OUTPUT_FLUID, elem);
  }

  public void set(int elem, @Nonnull Fluid input, @Nonnull Fluids output, int level, int amount) {
    set(COL_INPUT_FLUID, elem, input);
    set(COL_INPUT_FLUID_ID, elem, input.getID());
    set(COL_OUTPUT_FLUID, elem, output.getFluid());
    set(COL_OUTPUT_FLUID_ID, elem, output.getFluid().getID());
    set(COL_LEVEL, elem, level);
    set(COL_OUTPUT_AMOUNT, elem, amount);
  }

  public void set(int elem, @Nonnull Fluids input, @Nonnull Fluids output, int level, int amount) {
    set(COL_INPUT_FLUID, elem, input.getFluid());
    set(COL_INPUT_FLUID_ID, elem, input.getFluid().getID());
    set(COL_OUTPUT_FLUID, elem, output.getFluid());
    set(COL_OUTPUT_FLUID_ID, elem, output.getFluid().getID());
    set(COL_LEVEL, elem, level);
    set(COL_OUTPUT_AMOUNT, elem, amount);
  }

  public void set(int elem, @Nonnull Fluids input, int level) {
    set(COL_INPUT_FLUID, elem, input.getFluid());
    set(COL_INPUT_FLUID_ID, elem, input.getFluid().getID());
    set(COL_OUTPUT_FLUID, elem, null);
    set(COL_OUTPUT_FLUID_ID, elem, null);
    set(COL_LEVEL, elem, level);
    set(COL_OUTPUT_AMOUNT, elem, 0);
  }

}
