package info.loenwind.enderioaddons.machine.rlever;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;

public class BlockRLever extends BlockLever {

  private static List<Integer> delays = null;
  private static List<Block> blocks = null;

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
    getLevers();
    blocks = new ArrayList<>();
    for (Integer value : delays) {
      Block lever = new BlockRLever().setDelay(value * 20).setBlockName("rlever" + value);
      GameRegistry.registerBlock(lever, "rlever" + value);
      blocks.add(lever);
    }
  }

  public static List<Integer> getLevers() {
    if (delays == null) {
      delays = new ArrayList<>();
      String s = info.loenwind.enderioaddons.config.Config.leversEnabled.getString();
      s = s.replaceAll("[^0-9,]", "");
      String[] split = s.split(",");
      for (String string : split) {
        if (string != null && !string.isEmpty()) {
          try {
            final Integer value = Integer.valueOf(string);
            if (value > 0 && value <= 60 * 60 * 24) { // max 1 day
              delays.add(value);
            }
          } catch (NumberFormatException e) {
            Log.error("Could not parse lever time setting '" + string + "'");
          }
        }
      }
      Collections.sort(delays);
    }
    return delays;
  }

  public static List<Block> getBlocks() {
    return blocks;
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
