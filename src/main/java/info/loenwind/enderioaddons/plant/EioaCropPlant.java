package info.loenwind.enderioaddons.plant;

import static info.loenwind.enderioaddons.config.Config.seedsAutomaticHarvestingEnabled;
import static info.loenwind.enderioaddons.config.Config.seedsBonemealEnabled;
import static info.loenwind.enderioaddons.config.Config.seedsTierEasy;
import static info.loenwind.enderioaddons.config.Config.seedsTierHard;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.config.ItemHelper;
import info.loenwind.enderioaddons.config.WeightedItemStack;
import info.loenwind.enderioaddons.machine.afarm.BlockAfarm;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.ItemWithMeta;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.machine.farm.PacketFarmAction;
import crazypants.enderio.network.PacketHandler;

public class EioaCropPlant implements ICropPlant {

  private final ItemStack seed;
  public final EioaGrowthRequirement eioaGrowthRequirement = new EioaGrowthRequirement();

  public EioaCropPlant() {
    seed = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.SEED.ordinal());
  }

  @Override
  public int tier() {
    return crazypants.enderio.config.Config.useHardRecipes ? seedsTierHard.getInt() : seedsTierEasy.getInt();
  }

  @Override
  public ItemStack getSeed() {
    return seed;
  }

  @Override
  public Block getBlock() {
    return null;
  }

  @Override
  public ArrayList<ItemStack> getAllFruits() {
    ArrayList<ItemStack> allfruits = new ArrayList<>();
    for (WeightedItemStack fruit : ItemHelper.readWeightedList(Config.plantDropsHighGain)) {
      allfruits.add(fruit.getStack());
    }
    for (WeightedItemStack fruit : ItemHelper.readWeightedList(Config.plantDropsLowGain)) {
      if (!contains(allfruits, fruit.getStack())) {
        allfruits.add(fruit.getStack());
      }
    }
    return allfruits;
  }

  private static boolean contains(List<ItemStack> list, ItemStack stack) {
    for (ItemStack itemStack : list) {
      if (itemStack.isItemEqual(stack)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ItemStack getRandomFruit(Random rand) {
    return ((WeightedItemStack) WeightedRandom.getRandomItem(rand, ItemHelper.readWeightedList(Config.plantDropsHighGain))).getStack().copy();
  }

  @Override
  public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
    ArrayList<ItemStack> result = new ArrayList<>();
    for (int i = 0; i < (gain > 2 ? 2 : 1); i++) {
      result.add(((WeightedItemStack) WeightedRandom.getRandomItem(rand, ItemHelper.readWeightedList(Config.plantDropsLowGain))).getStack().copy());
    }
    if (gain > 5) {
      for (int i = 0; i < (gain == 10 ? 2 : 1); i++) {
        result.add(getRandomFruit(rand));
      }
    }
    return result;
  }

  @Override
  public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
    return (player != null && !(player instanceof FakePlayer)) || seedsAutomaticHarvestingEnabled.getBoolean();
  }

  @Override
  public void onSeedPlanted(World world, int x, int y, int z) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(new BlockCoord(x, y, z)), new TargetPoint(world.provider.dimensionId, x, y, z, 64));
  }

  @Override
  public void onPlantRemoved(World world, int x, int y, int z) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(new BlockCoord(x, y, z)), new TargetPoint(world.provider.dimensionId, x, y, z, 64));
  }

  @Override
  public boolean canBonemeal() {
    return seedsBonemealEnabled.getBoolean();
  }

  @Override
  public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
    if (!isMature(oldGrowthStage)) {
      eioaGrowthRequirement.usePower(world, x, y, z);
      PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(new BlockCoord(x, y, z)), new TargetPoint(world.provider.dimensionId, x, y, z, 64));
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isFertile(World world, int x, int y, int z) {
    return eioaGrowthRequirement.canGrow(world, x, y, z);
  }

  @Override
  public boolean isMature(IBlockAccess world, int x, int y, int z) {
    return isMature(world.getBlockMetadata(x, y, z));
  }

  private static boolean isMature(int meta) {
    return meta >= 7;
  }

  @Override
  public float getHeight(int meta) {
    return 13f / 16f;
  }

  @Override
  public IIcon getPlantIcon(int growthStage) {
    return BlockAfarm.darkPlantIcons[growthStage];
  }

  @Override
  public boolean renderAsFlower() {
    return false;
  }

  @Override
  public String getInformation() {
    return "enderioaddons.agricraft_journal.darkplant";
  }

  @Override
  public boolean overrideRendering() {
    return false;
  }

  @Override
  public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
  }

  public ItemWithMeta getSeedIWM() {
    return new ItemWithMeta(seed.getItem(), seed.getItemDamage());
  }

  public IGrowthRequirement getGrowthRequirement() {
    return eioaGrowthRequirement;
  }

}
