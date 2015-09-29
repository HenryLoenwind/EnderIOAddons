package info.loenwind.enderioaddons.machine.ihopper;

import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

public class RedstoneModeState {

  private RedstoneMode mode;
  private PrevStates prevState = PrevStates.NONE;

  private static enum PrevStates {
    NONE, OFF, ON,
  };

  public RedstoneModeState(RedstoneMode mode) {
    this.mode = mode;
  }

  public void setMode(RedstoneMode mode) {
    this.mode = mode;
    prevState = PrevStates.NONE;
  }

  public boolean isConditionMet(World world, BlockCoord bc) {
    return isConditionMet(world.getStrongestIndirectPower(bc.x, bc.y, bc.z));
  }

  public boolean isConditionMet(World world, int x, int y, int z) {
    return isConditionMet(world.getStrongestIndirectPower(x, y, z));
  }

  public boolean isConditionMet(int powerLevel) {
    switch (mode) {
    case IGNORE:
      return true;
    case NEVER:
      return false;
    case OFF:
      return powerLevel < 1;
    case ON:
      return powerLevel > 0;
    case RISING_EDGE:
      if (prevState == PrevStates.NONE) {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return false;
      } else if (prevState == PrevStates.OFF) {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return powerLevel > 0;
      } else {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return !(powerLevel > 0);
      }
    case FALLING_EDGE:
      if (prevState == PrevStates.NONE) {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return false;
      } else if (prevState == PrevStates.ON) {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return powerLevel > 0;
      } else {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return !(powerLevel > 0);
      }
    case HIGH_PULSE:
      if (prevState == PrevStates.NONE) {
        prevState = powerLevel == 0 ? PrevStates.OFF : PrevStates.ON;
        return false;
      } else if (prevState == PrevStates.OFF) {
        if (powerLevel >= 14) {
          prevState = PrevStates.ON;
          return true;
        }
        return false;
      } else {
        if (powerLevel == 0) {
          prevState = PrevStates.OFF;
        }
        return false;
      }
    case LOW_PULSE:
      if (prevState == PrevStates.NONE) {
        prevState = powerLevel >= 14 ? PrevStates.ON : PrevStates.OFF;
        return false;
      } else if (prevState == PrevStates.ON) {
        if (powerLevel == 0) {
          prevState = PrevStates.OFF;
          return true;
        }
        return false;
      } else {
        if (powerLevel >= 14) {
          prevState = PrevStates.ON;
        }
        return false;
      }
    default:
      break;
    }
    throw new RuntimeException("enum has unexpected values");
  }

}
