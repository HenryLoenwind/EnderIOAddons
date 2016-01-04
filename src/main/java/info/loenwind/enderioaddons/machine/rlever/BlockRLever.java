package info.loenwind.enderioaddons.machine.rlever;

import info.loenwind.enderioaddons.EnderIOAddons;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;

public class BlockRLever extends BlockLever {

  public static Block blockRLever10;
  public static Block blockRLever30;
  public static Block blockRLever60;
  public static Block blockRLever300;

  private int delay = 1;

  public Block setDelay(int delay) {
    this.delay = delay;
    return this;
  }

  public BlockRLever() {
    super();
    this.setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.5F);
    setStepSound(soundTypeWood);
    setBlockTextureName("lever");
  }

  public static void create() {
    blockRLever10 = new BlockRLever().setDelay(10 * 20).setBlockName("rlever10");
    GameRegistry.registerBlock(blockRLever10, "rlever10");
    blockRLever30 = new BlockRLever().setDelay(30 * 20).setBlockName("rlever30");
    GameRegistry.registerBlock(blockRLever30, "rlever30");
    blockRLever60 = new BlockRLever().setDelay(60 * 20).setBlockName("rlever60");
    GameRegistry.registerBlock(blockRLever60, "rlever60");
    blockRLever300 = new BlockRLever().setDelay(300 * 20).setBlockName("rlever300");
    GameRegistry.registerBlock(blockRLever300, "rlever300");
  }

  @Override
  public Block setBlockName(String p_149663_1_) {
    return super.setBlockName(EnderIOAddons.DOMAIN + "." + p_149663_1_);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
    if (world.isRemote) {
      return true;
    } else {
      if ((world.getBlockMetadata(x, y, z) & 8) == 0) {
        world.scheduleBlockUpdate(x, y, z, this, delay);
      }
      return super.onBlockActivated(world, x, y, z, player, side, hitx, hity, hitz);
    }
  }

  @Override
  public void updateTick(World world, int x, int y, int z, Random rand) {
    if ((world.getBlockMetadata(x, y, z) & 8) > 0) {
      onBlockActivated(world, x, y, z, null, 0, 0f, 0f, 0f);
    }
  }

}
