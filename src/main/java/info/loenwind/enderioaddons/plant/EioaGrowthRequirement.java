package info.loenwind.enderioaddons.plant;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;

import cpw.mods.fml.common.registry.GameRegistry.ItemStackHolder;
import crazypants.enderio.power.IPowerStorage;

public class EioaGrowthRequirement implements IGrowthRequirement {

  @ItemStackHolder(value = "minecraft:bedrock")
  public static final ItemStack bedrockStack = null;
  @ItemStackHolder(value = "EnderIO:blockDarkIronBars", meta = 0)
  public static final ItemStack darkBarStack = null;
  @ItemStackHolder(value = "EnderIO:blockCapBank")
  public static final ItemStack capBankStack = null;
  private BlockWithMeta capBank, bedrock, darkBar;
  private final int[] brightness = { 0, 0 };

  public EioaGrowthRequirement() {
    capBank = new BlockWithMeta(Block.getBlockFromItem(capBankStack.getItem()), OreDictionary.WILDCARD_VALUE, true);
    bedrock = new BlockWithMeta(Block.getBlockFromItem(bedrockStack.getItem()), OreDictionary.WILDCARD_VALUE, true);
    darkBar = new BlockWithMeta(Block.getBlockFromItem(darkBarStack.getItem()), OreDictionary.WILDCARD_VALUE, true);
  }

  @Override
  public boolean canGrow(World world, int x, int y, int z) {
    return isValidSoil(world, x, y, z) && isBaseBlockPresent(world, x, y, z) && areBarsPresent(world, x, y, z) && isPowerPresent(world, x, y, z);
  }

  public static boolean isPowerPresent(World world, int x, int y, int z) {
    TileEntity tileEntity = world.getTileEntity(x, y, z);
    if (tileEntity instanceof IPowerStorage) {
      IPowerStorage te = (IPowerStorage) tileEntity;
      return te.getEnergyStoredL() > 100000L; // TODO cfg
    }
    return false;
  }

  public static boolean usePower(World world, int x, int y, int z) {
    TileEntity tileEntity = world.getTileEntity(x, y, z);
    if (tileEntity instanceof IPowerStorage) {
      IPowerStorage te = (IPowerStorage) tileEntity;
      if (te.getEnergyStoredL() > 100000L) { // TODO cfg
        te.addEnergy(-100000); // TODO cfg
        return true;
      }
    }
    return false;
  }

  public boolean areBarsPresent(World world, int x, int y, int z) {
    for (int x1 = x - 1; x1 <= x + 1; x1++) {
      for (int z1 = z - 1; z1 <= z + 1; z1++) {
        if (x1 != 0 || z1 != 0) {
          Block block = world.getBlock(x1, y, z1);
          if (block != darkBar.getBlock()) {
            return false;
          }
          if (!darkBar.ignoreMeta() && world.getBlockMetadata(x1, y, z1) != darkBar.getMeta()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @Override
  public boolean isBaseBlockPresent(World world, int x, int y, int z) {
    Block block = world.getBlock(x, y - 1, z);
    if (block != bedrock.getBlock()) {
      return false;
    }
    if (bedrock.ignoreMeta()) {
      return true;
    }
    return world.getBlockMetadata(x, y - 1, z) == bedrock.getMeta();
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
    return bedrockStack;
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
