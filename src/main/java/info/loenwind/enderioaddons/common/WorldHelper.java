package info.loenwind.enderioaddons.common;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

public class WorldHelper {

  private final IBlockAccess world;

  public WorldHelper(IBlockAccess world) {
    this.world = world;
  }

  public static boolean isInWorld(World world, BlockCoord bc) {
    return world.blockExists(bc.x, bc.y, bc.z);
  }

  public boolean isInWorld(BlockCoord bc) {
    if (world instanceof World) {
      return bc != null && ((World) world).blockExists(bc.x, bc.y, bc.z);
    }
    return bc != null && bc.y > 0 && bc.y <= 255;
  }

  public static @Nullable Block getBlock(World world, BlockCoord bc) {
    if (bc != null && world.blockExists(bc.x, bc.y, bc.z)) {
      return world.getBlock(bc.x, bc.y, bc.z);
    }
    return null;
  }

  public static @Nullable Block getBlock(World world, int x, int y, int z) {
    if (world.blockExists(x, y, z)) {
      return world.getBlock(x, y, z);
    }
    return null;
  }

  public static @Nullable Block getBlock(IBlockAccess world, BlockCoord bc) {
    if (world instanceof World) {
      return getBlock((World) world, bc);
    }
    return bc != null ? world.getBlock(bc.x, bc.y, bc.z) : null;
  }

  public static @Nullable Block getBlock(IBlockAccess world, int x, int y, int z) {
    if (world instanceof World) {
      return getBlock((World) world, x, y, z);
    }
    return world.getBlock(x, y, z);
  }

  public @Nullable Block getBlock(BlockCoord bc) {
    if (world instanceof World) {
      return getBlock((World) world, bc);
    }
    return bc != null ? world.getBlock(bc.x, bc.y, bc.z) : null;
  }

  public @Nullable Block getBlock(int x, int y, int z) {
    if (world instanceof World) {
      return getBlock((World) world, x, y, z);
    }
    return world.getBlock(x, y, z);
  }

  public static int getMeta(World world, BlockCoord bc) {
    if (bc != null && world.blockExists(bc.x, bc.y, bc.z)) {
      return world.getBlockMetadata(bc.x, bc.y, bc.z);
    }
    return 0;
  }

  public static int getMeta(World world, int x, int y, int z) {
    if (world.blockExists(x, y, z)) {
      return world.getBlockMetadata(x, y, z);
    }
    return 0;
  }

  public static int getMeta(IBlockAccess world, BlockCoord bc) {
    if (world instanceof World) {
      return getMeta((World) world, bc);
    }
    return bc != null ? world.getBlockMetadata(bc.x, bc.y, bc.z) : 0;
  }

  public static int getMeta(IBlockAccess world, int x, int y, int z) {
    if (world instanceof World) {
      return getMeta((World) world, x, y, z);
    }
    return world.getBlockMetadata(x, y, z);
  }

  public int getMeta(BlockCoord bc) {
    if (world instanceof World) {
      return getMeta((World) world, bc);
    }
    return bc != null ? world.getBlockMetadata(bc.x, bc.y, bc.z) : 0;
  }

  public int getMeta(int x, int y, int z) {
    if (world instanceof World) {
      return getMeta((World) world, x, y, z);
    }
    return world.getBlockMetadata(x, y, z);
  }

  public static boolean isAir(World world, BlockCoord bc) {
    if (bc != null && world.blockExists(bc.x, bc.y, bc.z)) {
      return world.isAirBlock(bc.x, bc.y, bc.z);
    }
    return false;
  }

  public static boolean isAir(World world, int x, int y, int z) {
    if (world.blockExists(x, y, z)) {
      return world.isAirBlock(x, y, z);
    }
    return false;
  }

  public static boolean isAir(IBlockAccess world, BlockCoord bc) {
    if (world instanceof World) {
      return isAir((World) world, bc);
    }
    return bc != null ? world.isAirBlock(bc.x, bc.y, bc.z) : false;
  }

  public static boolean isAir(IBlockAccess world, int x, int y, int z) {
    if (world instanceof World) {
      return isAir((World) world, x, y, z);
    }
    return world.isAirBlock(x, y, z);
  }

  public boolean isAir(BlockCoord bc) {
    if (world instanceof World) {
      return isAir((World) world, bc);
    }
    return bc != null ? world.isAirBlock(bc.x, bc.y, bc.z) : false;
  }

  public boolean isAir(int x, int y, int z) {
    if (world instanceof World) {
      return isAir((World) world, x, y, z);
    }
    return world.isAirBlock(x, y, z);
  }
}
