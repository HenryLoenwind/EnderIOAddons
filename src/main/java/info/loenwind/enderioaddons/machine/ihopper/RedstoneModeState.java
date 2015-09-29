package info.loenwind.enderioaddons.machine.ihopper;

import info.loenwind.autosave.IHandler;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.enderioaddons.common.NullHelper;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

@Storable(handler = RedstoneModeState.class)
public class RedstoneModeState implements IHandler<RedstoneModeState> {

  private RedstoneMode mode;
  private PrevStates prevState = PrevStates.NONE;

  private static enum PrevStates {
    NONE, OFF, ON,
  };

  public RedstoneModeState(RedstoneMode mode) {
    this.mode = mode;
  }

  public void setMode(RedstoneMode mode) {
    if (this.mode != mode) {
      this.mode = mode;
      prevState = PrevStates.NONE;
    }
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

  @Override
  public boolean canHandle(Class<?> clazz) {
    return RedstoneModeState.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, RedstoneModeState object) throws IllegalArgumentException,
      IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("mode", object.mode.ordinal());
    tag.setInteger("prevState", object.prevState.ordinal());
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public RedstoneModeState read(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, RedstoneModeState object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    RedstoneModeState result = object != null ? object : new RedstoneModeState(RedstoneMode.values()[0]);
    if (nbt.hasKey(name)) {
      NBTTagCompound tag = NullHelper.notnullM(nbt.getCompoundTag(name), "NBTTagCompound.getCompoundTag()");
      result.mode = RedstoneMode.values()[tag.hasKey("mode") ? tag.getInteger("mode") : 0];
      result.prevState = PrevStates.values()[tag.hasKey("prevState") ? tag.getInteger("prevState") : 0];
    }
    return result;
  }

}
