package info.loenwind.enderioaddons.machine.drain;

import static info.loenwind.enderioaddons.common.NullHelper.notnull;
import static info.loenwind.enderioaddons.common.NullHelper.notnullF;
import info.loenwind.enderioaddons.fluid.FluidType;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import com.enderio.core.common.util.BlockCoord;

public final class FluidHelper {

  @Nonnull
  private final World world;
  @Nonnull
  private final FluidStack stack;
  @Nonnull
  private final Fluid fluid;
  @Nonnull
  private final Block block;
  @Nonnull
  private final ForgeDirection downflowDirection;
  @Nonnull
  private final ForgeDirection upflowDirection;
  @Nonnull
  private final FluidType type;
  @Nullable
  private final BlockCoord startbc;
  private IDrainingCallback hook;

  /*
   * Set this to prevent removed water from forming infinite pools. The block
   * expires when the given te is gc()ed.
   */
  public void setDrainingCallback(@Nonnull IDrainingCallback hook) {
    this.hook = hook;
  }

  private FluidHelper(@Nonnull World world, @Nonnull FluidStack stack, @Nullable BlockCoord startbc) throws Exception {
    this.world = world;
    this.stack = stack;
    this.fluid = notnullF(stack.getFluid(), "FluidStack.getFluid()");
    this.block = notnull(fluid.getBlock(), "Invalid Fluid (it has no source block)");
    this.downflowDirection = fluid.getDensity() > 0 ? ForgeDirection.DOWN : ForgeDirection.UP;
    this.upflowDirection = downflowDirection == ForgeDirection.UP ? ForgeDirection.DOWN : ForgeDirection.UP;
    if (this.block instanceof BlockFluidClassic) {
      this.type = FluidType.CLASSIC;
    } else if (this.block instanceof BlockFluidFinite) {
      this.type = FluidType.FINITE;
    } else if (this.block instanceof BlockLiquid) {
      this.type = FluidType.VANILLA;
    } else {
      throw new Exception();
    }
    this.startbc = startbc;
  }

  public static boolean isSourceBlock(@Nonnull World world, @Nonnull BlockCoord bc) {
    Block block = bc.getBlock(world);
    if (block instanceof BlockFluidClassic) {
      return ((BlockFluidClassic) block).isSourceBlock(world, bc.x, bc.y, bc.z);
    } else if (block instanceof BlockFluidFinite) {
      return ((BlockFluidFinite)block).canDrain(world, bc.x, bc.y, bc.z);
    } else if (block instanceof BlockLiquid) {
      return world.getBlockMetadata(bc.x, bc.y, bc.z) == 0;
    } else {
      return false;
    }
  }

  @Nonnull
  private static final ForgeDirection[] DIRECTIONS_INIT = { ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST,
      ForgeDirection.EAST };
  
  @Nullable
  public static FluidHelper getInstance(@Nonnull World world, @Nonnull BlockCoord bc) {
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = getLocation(bc, forgeDirection);
      if (isSourceBlock(world, direction)) {
        Fluid fluidForBlock = FluidRegistry.lookupFluidForBlock(direction.getBlock(world));
        if (fluidForBlock != null) {
          FluidHelper result = getInstance(world, new FluidStack(fluidForBlock, 1000), direction);
          if (result != null) {
            return result;
          }
        }
      }
    }
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = getLocation(bc, forgeDirection);
      Fluid fluidForBlock = FluidRegistry.lookupFluidForBlock(direction.getBlock(world));
      if (fluidForBlock != null) {
        FluidHelper result = getInstance(world, new FluidStack(fluidForBlock, 1000), direction);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }
  
  @Nullable
  public static FluidHelper getInstance(@Nonnull World world, @Nonnull BlockCoord bc, @Nonnull FluidStack fs) {
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = getLocation(bc, forgeDirection);
      if (isSourceBlock(world, direction) && isSameLiquid(fs, world, direction)) {
        return getInstance(world, fs, direction);
      }
    }
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = getLocation(bc, forgeDirection);
      if (isSameLiquid(fs, world, direction)) {
        return getInstance(world, fs, direction);
      }
    }
    return null;
  }
  
  @Nullable
  public static FluidHelper getInstance(@Nonnull World world, @Nonnull FluidStack stack, @Nullable BlockCoord startbc) {
    try {
      return new FluidHelper(world, stack, startbc);
    } catch (Throwable t) {
      // NPE for fluids that have no block
      // E for fluid that don't extend one of the known classses
      return null;
    }
  }

  @Nullable
  public static FluidHelper getInstance(@Nonnull World world, @Nonnull FluidStack stack) {
    return getInstance(world, stack, null);
  }

  private static boolean isInWorld(@Nonnull BlockCoord bc) {
    return bc.y > 0 && bc.y <= 255;
  }
  
  /*
   * same liquid
   */
  public boolean isSameLiquid(@Nonnull BlockCoord bc) {
    return bc.getBlock(world) == block;
  }
  
  public static boolean isSameLiquid(@Nonnull FluidStack fs, @Nonnull World world, @Nonnull BlockCoord bc) {
    return bc.getBlock(world) == fs.getFluid().getBlock();
  }
  
  public boolean isSourceBlock(@Nonnull BlockCoord bc) {
    switch (type) {
    case CLASSIC:
      return ((BlockFluidClassic) block).isSourceBlock(world, bc.x, bc.y, bc.z);
    case FINITE:
      return false;
    case VANILLA:
      return world.getBlockMetadata(bc.x, bc.y, bc.z) == 0;
    }
    throw new IllegalStateException("unreachable code");
  }
  
  public boolean isFlowingBlock(@Nonnull BlockCoord bc) {
	    switch (type) {
	    case CLASSIC:
	      return !((BlockFluidClassic) block).isSourceBlock(world, bc.x, bc.y, bc.z);
	    case FINITE:
	      return false;
	    case VANILLA:
	      return world.getBlockMetadata(bc.x, bc.y, bc.z) != 0;
	    }
	    throw new IllegalStateException("unreachable code");
	  }
	  
  @Nonnull
  public static BlockCoord getLocation(@Nonnull BlockCoord bc, ForgeDirection dir) {
    notnull(dir, "ForgeDirection went AWOL");
    return new BlockCoord(bc.x + dir.offsetX, bc.y + dir.offsetY, bc.z + dir.offsetZ);
  }

  /*
   * Replacement for isFlowingVertically() that does the right thing
   */
  public boolean isFlowingVertically2(@Nonnull BlockCoord bc) {
    BlockCoord downflow = getLocation(bc, downflowDirection);
    return isSameLiquid(getLocation(bc, ForgeDirection.UP)) && isSameLiquid(getLocation(bc, ForgeDirection.DOWN)) && !isSourceBlock(downflow);
  }

  /*
   * same liquid and nearer to a source block
   */
  public boolean isUpflow(@Nonnull BlockCoord bc0, @Nonnull BlockCoord bc1) {
    switch (type) {
    case CLASSIC:
      return world.getBlockMetadata(bc1.x, bc1.y, bc1.z) < world.getBlockMetadata(bc0.x, bc0.y, bc0.z)
 || isSameLiquid(getLocation(bc1, upflowDirection));
    case FINITE:
      return world.getBlockMetadata(bc1.x, bc1.y, bc1.z) > world.getBlockMetadata(bc0.x, bc0.y, bc0.z);
    case VANILLA:
      return world.getBlockMetadata(bc1.x, bc1.y, bc1.z) < world.getBlockMetadata(bc0.x, bc0.y, bc0.z) 
    		  || (world.getBlockMetadata(bc1.x, bc1.y, bc1.z) & 8) != 0;
      // other block has higher level of liquid OR other block is downflow
    }
    throw new IllegalStateException("unreachable code");
  }

  /*
   * move a source block, or delete it if it flows out of the world
   */
  public void doFlow(@Nonnull BlockCoord bc0, @Nonnull BlockCoord bc1) {
    if (isInWorld(bc1)) {
      world.setBlock(bc1.x, bc1.y, bc1.z, world.getBlock(bc0.x, bc0.y, bc0.z), world.getBlockMetadata(bc0.x, bc0.y, bc0.z), 3);
    }
    switch (type) {
    case FINITE:
      world.setBlockToAir(bc0.x, bc0.y, bc0.z);
      break;
    case CLASSIC:
    case VANILLA:
      if (adjCount(bc0) > 1) {
        world.setBlock(bc0.x, bc0.y, bc0.z, world.getBlock(bc0.x, bc0.y, bc0.z), 1, 3);
        if (fluid == FluidRegistry.WATER && hook != null) {
          preventWater(bc0);
          preventWater(bc1);
        }
      } else {
        world.setBlockToAir(bc0.x, bc0.y, bc0.z);
      }
      break;
    }
  }

  private void preventWater(@Nonnull BlockCoord bc) {
    for (ForgeDirection forgeDirection : DIRECTIONS) {
      final BlockCoord bc1 = getLocation(bc, forgeDirection);
      if (isSameLiquid(bc1) && !isSourceBlock(bc1)) {
        hook.onWaterDrainNearby(world, bc1);
      }
    }
    hook.onWaterDrain(world, bc);
  }
  
  private int adjCount(@Nonnull BlockCoord bc) {
    int result = 0;
    for (ForgeDirection forgeDirection : DIRECTIONS) {
      if (isSameLiquid(getLocation(bc, forgeDirection))) {
        result++;
      }
    }
    return result;
  }
  
  @Nonnull
  public static final ForgeDirection[] DIRECTIONS = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };
  
  private Set<BlockCoord> seen;
  
  public boolean findAndPullSourceBlock(@Nonnull BlockCoord bc) {
    seen = new HashSet<BlockCoord>();
    return findAndPullSourceBlock(bc, false);
  }

  public static class ReturnObject {
    public boolean isDry = false;
    public boolean inProgress = false;
    @Nullable
    public FluidStack result = null;
  }

  @Nonnull
  public ReturnObject eatOrPullFluid() {
    return eatOrPullFluid(notnull(startbc, "FluidHelper starting position has not been set"));
  }

  @Nonnull
  public ReturnObject eatOrPullFluid(@Nonnull BlockCoord bc) {
    ReturnObject result = new ReturnObject();
    if (isSameLiquid(bc)) {
      if (!isSourceBlock(bc)) {
        seen = new HashSet<BlockCoord>();
        result.inProgress = findAndPullSourceBlock(bc, true);
      }
      if (isSourceBlock(bc)) {
        switch (type) {
        case CLASSIC:
        case FINITE:
          if (((IFluidBlock) block).canDrain(world, bc.x, bc.y, bc.z)) {
            result.result = ((IFluidBlock) block).drain(world, bc.x, bc.y, bc.z, true);
          } else {
            result.isDry = true;
          }
          break;
        case VANILLA:
          result.result = stack.copy();
          notnullF(result.result, "FluidStack.copy()").amount = 1000;
          if (fluid == FluidRegistry.WATER && hook != null) {
            hook.onWaterDrain(world, bc);
          }
          world.setBlockToAir(bc.x, bc.y, bc.z);
          break;
        default:
          throw new IllegalStateException("unreachable code");
        }
      }
      if (!result.inProgress && result.result == null) {
        result.isDry = true;
        // there is liquid here but we were unable to find a source block for it. Minecraft's fluid mechanics
        // may be messed up and give us some fake flowing blocks. Try to remedy this be forcing it to re-flow them.
        for (BlockCoord blockCoord : seen) {
          blockCoord = notnull(blockCoord, "FluidHelper is confused: I remember to have visited an invalid location?");
          if (isFlowingBlock(blockCoord) && isSameLiquid(blockCoord)) {
            world.setBlockToAir(blockCoord.x, blockCoord.y, blockCoord.z);
          }
        }
      }
    } else {
      result.isDry = true;
    }
    return result;
  }
  
  private boolean findAndPullSourceBlock(@Nonnull BlockCoord bc, boolean foundStepUp) {
    if (!seen.contains(bc)) {
      seen.add(bc);

      BlockCoord upflow = getLocation(bc, upflowDirection);

      // try to go up first
      if (isInWorld(upflow) && isSameLiquid(upflow)) {
        if (isSourceBlock(upflow)) {
          doFlow(upflow, bc);
          return true;
        } else if (findAndPullSourceBlock(upflow, true)) {
          return true;
        }
      }

      // then look around
      for (ForgeDirection dir : DIRECTIONS) {
        BlockCoord bc2 = getLocation(bc, dir);
        if (isSameLiquid(bc2)) {
          if (isSourceBlock(bc2)) {
            if (foundStepUp) { // don't flow unless there is a "down" to flow to
              if (isSameLiquid(getLocation(bc2, downflowDirection)) && !isSourceBlock(getLocation(bc2, downflowDirection))
                  && isSameLiquid(getLocation(bc, downflowDirection)) && !isSourceBlock(getLocation(bc, downflowDirection))) {
                // if we can drop the source block down by one without disconnecting it from us, we do so
                doFlow(bc2, getLocation(bc2, downflowDirection));
              } else {
                doFlow(bc2, bc);
              }
            }
            return true;
          } else if (isUpflow(bc, bc2) && !isFlowingVertically2(bc2)) {
            if (findAndPullSourceBlock(bc2, foundStepUp)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
}
