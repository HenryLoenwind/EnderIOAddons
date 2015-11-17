package info.loenwind.enderioaddons.plant;

import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;

public class EioaCropPlant implements ICropPlant {

  public EioaCropPlant() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public int tier() {
    return 5;
  }

  @Override
  public ItemStack getSeed() {
    return new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.SEED.ordinal());
  }

  @Override
  public Block getBlock() {
    return null;
  }

  @Override
  public ArrayList<ItemStack> getAllFruits() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ItemStack getRandomFruit(Random rand) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void onSeedPlanted(World world, int x, int y, int z) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPlantRemoved(World world, int x, int y, int z) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean canBonemeal() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isFertile(World world, int x, int y, int z) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isMature(IBlockAccess world, int x, int y, int z) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public float getHeight(int meta) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public IIcon getPlantIcon(int growthStage) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean renderAsFlower() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getInformation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean overrideRendering() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
    // TODO Auto-generated method stub

  }

}
