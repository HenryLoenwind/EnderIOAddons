package info.loenwind.enderioaddons.plant;

import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.recipe.Recipes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;

import crazypants.enderio.power.IPowerStorage;

public class EioaGrowthRequirement implements IGrowthRequirement {

  private BlockWithMeta capBank, bedrock, darkBar;
  private final int[] brightness = { 0, 0 };

  public EioaGrowthRequirement() {
    System.out.println(Recipes.capBankCreative + "/" + Blocks.bedrock + "/" + Recipes.darkSteelBars);
    capBank = new BlockWithMeta(Block.getBlockFromItem(Recipes.capBankCreative.getItem()), OreDictionary.WILDCARD_VALUE, true);
    bedrock = new BlockWithMeta(Blocks.bedrock, OreDictionary.WILDCARD_VALUE, true);
    darkBar = new BlockWithMeta(Block.getBlockFromItem(Recipes.darkSteelBars.getItem()), OreDictionary.WILDCARD_VALUE, true);
    try {
      Class<?> growthRequirementHandler = Class.forName("com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler");
      if (growthRequirementHandler != null) {
        Method addSoil = growthRequirementHandler.getMethod("addSoil", BlockWithMeta.class);
        if (addSoil != null) {
          addSoil.invoke(null, capBank);
        }
      }
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Log.warn("Failed to register soil with AgriCraft. Error:");
      e.printStackTrace();
    }
  }

  @Override
  public boolean canGrow(World world, int x, int y, int z) { // TODO: light level
    return isValidSoil(world, x, y - 1, z) && isBaseBlockPresent(world, x, y, z) && areBarsPresent(world, x, y, z);
  }

  public static boolean usePower(World world, int x, int y, int z) {
    TileEntity tileEntity = world.getTileEntity(x, y - 1, z);
    if (tileEntity instanceof IPowerStorage) {
      IPowerStorage te = (IPowerStorage) tileEntity;
      if (te.getEnergyStoredL() >= 100000L) { // TODO cfg
        te.addEnergy(-100000); // TODO cfg
        return true;
      }
    }
    return false;
  }

  public boolean areBarsPresent(World world, int x, int y, int z) {
    int count = 0;
    for (int x1 = x - 1; x1 <= x + 1; x1++) {
      for (int z1 = z - 1; z1 <= z + 1; z1++) {
        if (x1 != x || z1 != z) {
          Block block = world.getBlock(x1, y, z1);
          if (block == darkBar.getBlock() && (darkBar.ignoreMeta() || world.getBlockMetadata(x1, y, z1) == darkBar.getMeta())) {
            if (++count >= 6) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean isBaseBlockPresent(World world, int x, int y, int z) {
    Block block = world.getBlock(x, y - 2, z);
    if (block != bedrock.getBlock()) {
      return false;
    }
    if (bedrock.ignoreMeta()) {
      return true;
    }
    return world.getBlockMetadata(x, y - 2, z) == bedrock.getMeta();
  }

  @Override
  public boolean isValidSoil(World world, int x, int y, int z) {
    Block block = world.getBlock(x, y, z);
    if (block != capBank.getBlock()) {
      return false;
    }
    if (capBank.ignoreMeta()) {
      return true;
    }
    return world.getBlockMetadata(x, y, z) == capBank.getMeta();
  }

  @Override
  public boolean isValidSoil(BlockWithMeta soil) {
    return capBank.equals(soil);
  }

  @Override
  public ItemStack requiredBlockAsItemStack() {
    return bedrock.toStack();
  }

  @Override
  public RequirementType getRequiredType() {
    return RequirementType.BELOW;
  }

  @Override
  public BlockWithMeta getSoil() {
    return capBank;
  }

  @Override
  public void setSoil(BlockWithMeta soil) {
  }

  @Override
  public int[] getBrightnessRange() {
    return brightness;
  }

  @Override
  public void setBrightnessRange(int min, int max) {
  }

  @Override
  public void setRequiredBlock(BlockWithMeta requiredBlock, RequirementType requirementType, boolean oreDict) {
  }

  @Override
  public BlockWithMeta getRequiredBlock() {
    return bedrock;
  }

  @Override
  public boolean isOreDict() {
    return true;
  }

}
