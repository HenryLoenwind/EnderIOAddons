package info.loenwind.enderioaddons.plant;

import static info.loenwind.enderioaddons.config.Config.seedsRFperGrowthTick;
import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.common.WorldHelper;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.recipe.Recipes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;

import crazypants.enderio.power.IPowerStorage;

public class EioaGrowthRequirement implements IGrowthRequirement {

  private BlockWithMeta capBank, bedrock, darkBar;
  private final int[] brightness = { 13, 15, 0, 0 };

  public EioaGrowthRequirement() {
    capBank = new BlockWithMeta(Block.getBlockFromItem(Recipes.capBankCreative.getItem()), 1, true);
    bedrock = new BlockWithMeta(Blocks.bedrock, OreDictionary.WILDCARD_VALUE, true);
    darkBar = new BlockWithMeta(Block.getBlockFromItem(Recipes.darkSteelBars.getItem()), OreDictionary.WILDCARD_VALUE, true);
    try {
      Class<?> newGrowthRequirementHandler = Class.forName("com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler");
      if (newGrowthRequirementHandler != null) {
        Log.debug("An newer version of AgriCraft is in use, our soil will be registered automatically.");
        return;
      }
    } catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
      Log.debug("An older version of AgriCraft is in use, manually registering our soil.");
    }
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
  public boolean canGrow(World world, int x, int y, int z) {
    if (Config.growthRequirementDebuggingEnabled.getBoolean()) {
      String txt = "Plant at " + x + "/" + y + "/" + z + ": ";
      if (isValidSoil(world, x, y - 1, z)) {
        txt += "Soil (" + capBank + ") is valid. ";
      } else {
        txt += "Soil (" + capBank + ") is NOT valid, it is " + WorldHelper.getBlock(world, x, y, z) + ":" + WorldHelper.getMeta(world, x, y, z) + ". ";
      }
      if (isBaseBlockPresent(world, x, y, z)) {
        txt += "Base block (" + bedrock + ") is valid directly below soil. ";
      } else if (isBaseBlockPresent(world, x, y - 1, z)) {
        txt += "Base block (" + bedrock + ") is valid one below soil. ";
      } else {
        txt += "Base block (" + bedrock + ") is MISSING. Block directly below soil is " + WorldHelper.getBlock(world, x, y - 1, z) + ":"
            + WorldHelper.getMeta(world, x, y - 1, z) + ", block below that is " + WorldHelper.getBlock(world, x, y - 2, z) + ":"
            + WorldHelper.getMeta(world, x, y - 2, z) + ". ";
      }
      if (areBarsPresent(world, x, y, z)) {
        txt += "Bars (" + darkBar + ") are valid. ";
      } else {
        txt += "Bars (" + darkBar + ") are NOT valid. ";
      }
      if (isBrightnessOk(world, x, y, z)) {
        txt += "Light levels (sky light " + brightness[2] + "..." + brightness[3] + " and block light " + brightness[0] + "..." + brightness[1] + ") are ok. ";
      } else {
        txt += "Light levels (sky light " + brightness[2] + "..." + brightness[3] + " and block light " + brightness[0] + "..." + brightness[1]
            + ") are NOT ok, sky light is " + world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) + ", block light is "
            + world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) + ". ";
      }
      if (world.isRemote) {
        txt += "Power level in the capacitor bank can only be checked server-side. ";
      } else if (hasPower(world, x, y, z)) {
        txt += "Power level in the capacitor bank is ok. ";
      } else {
        txt += "Power level in the capacitor bank is too low. ";
      }
      Log.info(txt);
    }
    return isValidSoil(world, x, y - 1, z) && (isBaseBlockPresent(world, x, y, z) || isBaseBlockPresent(world, x, y - 1, z)) && areBarsPresent(world, x, y, z)
        && isBrightnessOk(world, x, y, z) && hasPower(world, x, y, z);
  }

  public static boolean hasPower(World world, int x, int y, int z) {
    if (world.isRemote) {
      return true;
    }
    TileEntity tileEntity = world.getTileEntity(x, y - 1, z);
    if (tileEntity instanceof IPowerStorage) {
      IPowerStorage te = (IPowerStorage) tileEntity;
      if (te.getEnergyStoredL() >= seedsRFperGrowthTick.getInt()) {
        return true;
      }
    }
    return false;
  }

  public static boolean usePower(World world, int x, int y, int z) {
    TileEntity tileEntity = world.getTileEntity(x, y - 1, z);
    if (tileEntity instanceof IPowerStorage) {
      IPowerStorage te = (IPowerStorage) tileEntity;
      if (te.getEnergyStoredL() >= seedsRFperGrowthTick.getInt()) {
        te.addEnergy(-seedsRFperGrowthTick.getInt());
        return true;
      } else {
        te.addEnergy((int) -te.getEnergyStoredL());
        return false;
      }
    }
    return false;
  }

  public boolean areBarsPresent(World world, int x, int y, int z) {
    int count = 0;
    for (int x1 = x - 1; x1 <= x + 1; x1++) {
      for (int z1 = z - 1; z1 <= z + 1; z1++) {
        if (x1 != x || z1 != z) {
          if (isBlock(world, x1, y, z1, darkBar) || isBlock(world, x1, y, z1, bedrock)) {
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
    if (bedrock == null) {
      return true;
    }
    return isBlock(world, x, y - 2, z, bedrock);
  }

  private static boolean isBlock(World world, int x, int y, int z, BlockWithMeta bwm) {
    return WorldHelper.getBlock(world, x, y, z) == bwm.getBlock() && (bwm.ignoreMeta() || WorldHelper.getMeta(world, x, y, z) == bwm.getMeta());
  }

  @Override
  public boolean isValidSoil(World world, int x, int y, int z) {
    if (!world.blockExists(x, y, z)) {
      return false;
    }
    Block block = world.getBlock(x, y, z);
    if (block != capBank.getBlock()) {
      return false;
    }
    if (capBank.ignoreMeta()) {
      return true;
    }
    return world.getBlockMetadata(x, y, z) == capBank.getMeta();
  }

  public boolean isBrightnessOk(World world, int x, int y, int z) {
    int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
    int blockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
    return skyLight >= brightness[2] && skyLight <= brightness[3] && blockLight >= brightness[0] && blockLight <= brightness[1];
  }

  @Override
  public boolean isValidSoil(BlockWithMeta soil) {
    return capBank.equals(soil);
  }

  @Override
  public ItemStack requiredBlockAsItemStack() {
    return bedrock != null ? bedrock.toStack() : null;
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
    capBank = soil;
  }

  @Override
  public int[] getBrightnessRange() {
    return brightness;
  }

  @Override
  public void setBrightnessRange(int min, int max) {
    brightness[0] = min & 0x0f;
    brightness[1] = max & 0x0f;
    brightness[2] = min >> 4;
    brightness[3] = max >> 4;
  }

  @Override
  public void setRequiredBlock(BlockWithMeta requiredBlock, RequirementType requirementType, boolean oreDict) {
    bedrock = requiredBlock;
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
