package info.loenwind.enderioaddons.plant;

import static info.loenwind.enderioaddons.config.Config.seedsAutomaticHarvestingEnabled;
import static info.loenwind.enderioaddons.config.Config.seedsBonemealEnabled;
import static info.loenwind.enderioaddons.config.Config.seedsTierEasy;
import static info.loenwind.enderioaddons.config.Config.seedsTierHard;
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
import cpw.mods.fml.common.registry.GameRegistry.ItemStackHolder;
import crazypants.enderio.machine.farm.PacketFarmAction;
import crazypants.enderio.network.PacketHandler;

public class EioaCropPlant implements ICropPlant {

  @ItemStackHolder(value = "EnderIO:itemBasicCapacitor", meta = 0)
  public static final ItemStack capacitor1 = null;
  @ItemStackHolder(value = "EnderIO:itemBasicCapacitor", meta = 1)
  public static final ItemStack capacitor2 = null;
  @ItemStackHolder(value = "EnderIO:itemBasicCapacitor", meta = 2)
  public static final ItemStack capacitor8 = null;

  private final List<WeightedItemStack> fruits = new ArrayList<>();
  private final ArrayList<ItemStack> allfruits = new ArrayList<>();
  private final List<WeightedItemStack> lowfruits = new ArrayList<>();
  private final ItemStack seed;
  public final EioaGrowthRequirement eioaGrowthRequirement = new EioaGrowthRequirement();

  public EioaCropPlant() {
    lowfruits.add(new WeightedItemStack(13500, new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.SCS.ordinal())));
    lowfruits.add(new WeightedItemStack(10, capacitor1));
    lowfruits.add(new WeightedItemStack(5, capacitor2));
    lowfruits.add(new WeightedItemStack(1, capacitor8));
    fruits.add(new WeightedItemStack(10000, new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.SCS.ordinal())));
    fruits.add(new WeightedItemStack(3000, new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MCS.ordinal())));
    fruits.add(new WeightedItemStack(500, new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.LCS.ordinal())));
    fruits.add(new WeightedItemStack(10, capacitor1));
    fruits.add(new WeightedItemStack(5, capacitor2));
    fruits.add(new WeightedItemStack(1, capacitor8));
    for (WeightedItemStack fruit : fruits) {
      allfruits.add(fruit.stack);
    }
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
    return allfruits;
  }

  @Override
  public ItemStack getRandomFruit(Random rand) {
    return ((WeightedItemStack) WeightedRandom.getRandomItem(rand, fruits)).stack;
  }

  @Override
  public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
    ArrayList<ItemStack> result = new ArrayList<>();
    for (int i = 0; i < (gain > 2 ? 2 : 1); i++) {
      result.add(((WeightedItemStack) WeightedRandom.getRandomItem(rand, lowfruits)).stack);
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
    if (!isMature(world, x, y, z)) {
      eioaGrowthRequirement.usePower(world, x, y, z);
      PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(new BlockCoord(x, y, z)), new TargetPoint(world.provider.dimensionId, x, y, z, 64));
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isFertile(World world, int x, int y, int z) {
    return !isMature(world, x, y, z) && eioaGrowthRequirement.canGrow(world, x, y, z);
  }

  @Override
  public boolean isMature(IBlockAccess world, int x, int y, int z) {
    return world.getBlockMetadata(x, y, z) >= 7;
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

  private static class WeightedItemStack extends WeightedRandom.Item {

    private final ItemStack stack;

    public WeightedItemStack(int weight, ItemStack stack) {
      super(weight);
      this.stack = stack;
    }

  }

  public ItemWithMeta getSeedIWM() {
    return new ItemWithMeta(seed.getItem(), seed.getItemDamage());
  }

  public IGrowthRequirement getGrowthRequirement() {
    return eioaGrowthRequirement;
  }

}
