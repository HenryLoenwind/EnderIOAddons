package info.loenwind.enderioaddons.machine.waterworks;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.RecipeBonusType;

public class DummyRecipe implements IMachineRecipe {

  public static final DummyRecipe instance = new DummyRecipe();

  @Override
  public String getUid() {
    return BlockWaterworks.ModObject_blockWaterworks.unlocalisedName + "Recipe";
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return 1;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    return true;
  }

  @Override
  public ResultStack[] getCompletedResult(float randomChance, MachineRecipeInput... inputs) {
    return new ResultStack[0];
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
    return 0;
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    return false;
  }

  @Override
  public String getMachineName() {
    return BlockWaterworks.ModObject_blockWaterworks.unlocalisedName;
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    return Collections.emptyList();
  }

}