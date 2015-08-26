package info.loenwind.enderioaddons.machine.waterworks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.enderio.machine.spawner.DummyRecipe;

/**
 * A task that just runs for the given amount of energy. It has no recipe, input
 * or output.
 *
 */
public class TaskWaterworks implements IPoweredTask {

  private float usedEnergy = 0;
  private final float requiredEnergy;

  public TaskWaterworks(float requiredEnergy) {
    this.requiredEnergy = requiredEnergy;
  }

  private TaskWaterworks(float requiredEnergy, float usedEnergy) {
    this(requiredEnergy);
    this.usedEnergy = usedEnergy;
  }

  @Override
  public void update(float availableEnergy) {
    usedEnergy += availableEnergy;
  }

  @Override
  public boolean isComplete() {
    return usedEnergy >= requiredEnergy;
  }

  @Override
  public float getProgress() {
    return MathHelper.clamp_float(usedEnergy / requiredEnergy, 0, 1);
  }

  @Override
  public ResultStack[] getCompletedResult() {
    return new ResultStack[0];
  }

  @Override
  public float getRequiredEnergy() {
    return requiredEnergy;
  }

  @Override
  public float getChance() {
    return 0;
  }

  @Override
  public RecipeBonusType getBonusType() {
    return RecipeBonusType.NONE;
  }

  public static final String KEY_USED_ENERGY = "usedEnergy";
  public static final String KEY_REQUIRED_ENERGY = "requiredEnergy";

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setFloat(KEY_USED_ENERGY, usedEnergy);
    nbtRoot.setFloat(KEY_REQUIRED_ENERGY, requiredEnergy);
  }

  public static IPoweredTask readFromNBT(NBTTagCompound nbtRoot) {
    if (nbtRoot == null) {
      return null;
    }

    return new TaskWaterworks(nbtRoot.getFloat(KEY_REQUIRED_ENERGY), nbtRoot.getFloat(KEY_USED_ENERGY));
  }

  @Override
  public IMachineRecipe getRecipe() {
    return new DummyRecipe();
  }

  @Override
  public MachineRecipeInput[] getInputs() {
    return new MachineRecipeInput[0];
  }

}
