package info.loenwind.enderioaddons.machine.niard;

import info.loenwind.enderioaddons.fluid.FluidType;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.xp.XpUtil;

public class EngineNiard {

  private final TileNiard owner;
  private Fluid fluid = null;
  private Block block;
  private FluidType type;
  private ForgeDirection downflowDirection;
  private int radius = -1;
  private RadiusIterator radiusItr;

  public EngineNiard(TileNiard owner) {
    this.owner = owner;
  }

  public EngineNiard setFluid(Fluid fluid) {
    if (fluid.canBePlacedInWorld()) {
      this.radiusItr = radius >= 0 ? new RadiusIterator(owner.getLocation(), radius) : null;
      this.fluid = fluid;
      this.downflowDirection = fluid.getDensity() > 0 ? ForgeDirection.DOWN : ForgeDirection.UP;
      block = fluid.getBlock();
      if (block instanceof BlockFluidClassic) {
        type = FluidType.CLASSIC;
      } else if (block instanceof BlockFluidFinite) {
        type = FluidType.FINITE;
      } else if (block instanceof BlockLiquid) {
        type = FluidType.VANILLA;
      } else {
        this.fluid = null;
      }
    } else {
      this.fluid = null;
    }
    return this;
  }

  public EngineNiard setRadius(int radius) {
    if (radius != this.radius) {
      this.radius = radius;
      this.radiusItr = new RadiusIterator(owner.getLocation(), radius);
    }
    return this;
  }

  public boolean work() {
    if (fluid == null || radius < 0) {
      return false;
    }
    for (int i = 0; i < radiusItr.size(); i++) {
      List<BlockCoord> seen = new ArrayList<>();
      BlockCoord base = radiusItr.next();
      BlockCoord next = base.getLocation(downflowDirection);
      while (isInWorld(next) && (owner.getWorldObj().isAirBlock(next.x, next.y, next.z) || (isSameLiquid(next) && isFlowingBlock(next)))) {
        seen.add(next);
        next = next.getLocation(downflowDirection);
      }
      if (!seen.isEmpty()) {
        setSourceBlock(seen.remove(seen.size() - 1));
        if (!seen.isEmpty()) {
          for (BlockCoord bc : seen) {
            setVerticalBlock(bc, false);
          }
          owner.getWorldObj().notifyBlockChange(base.x, base.y, base.z, base.getBlock(owner.getWorldObj()));
        }
        return true;
      }
    }
    return false;
  }

  public int work(int xp_in_mb) {
    int remaining = XpUtil.liquidToExperience(xp_in_mb);
    if (radius >= 0) {
      for (int i = 0; i < radiusItr.size(); i++) {
        BlockCoord next = radiusItr.next().getLocation(ForgeDirection.DOWN);
        if (isInWorld(next) && owner.getWorldObj().isAirBlock(next.x, next.y, next.z)) {
          int i1 = EntityXPOrb.getXPSplit(remaining / (owner.getWorldObj().rand.nextInt(4) + 1));
          remaining -= i1;
          final EntityXPOrb xpOrb = new EntityXPOrb(owner.getWorldObj(), next.x + 0.5D, next.y + 0.7D, next.z + 0.5D, i1);
          xpOrb.motionX /= 4d;
          xpOrb.motionY = 0;
          xpOrb.motionZ /= 4d;
          owner.getWorldObj().spawnEntityInWorld(xpOrb);
          if (remaining <= 0) {
            return 0;
          }
        }
      }
    }
    return XpUtil.experienceToLiquid(remaining);
  }

  // Tools of the trade

  private static boolean isInWorld(BlockCoord bc) {
    return bc.y > 0 && bc.y <= 255;
  }

  private boolean isSameLiquid(@Nonnull BlockCoord bc) {
    final Block wblock = bc.getBlock(owner.getWorldObj());
    return wblock == block || (block == Blocks.water && wblock == Blocks.flowing_water) || (block == Blocks.lava && wblock == Blocks.flowing_lava);
  }

  private boolean isFlowingBlock(@Nonnull BlockCoord bc) {
    switch (type) {
    case CLASSIC:
      return !((BlockFluidClassic) block).isSourceBlock(owner.getWorldObj(), bc.x, bc.y, bc.z);
    case FINITE:
      return false;
    case VANILLA:
      return owner.getWorldObj().getBlockMetadata(bc.x, bc.y, bc.z) != 0;
    }
    throw new IllegalStateException("unreachable code");
  }

  private void setSourceBlock(BlockCoord bc) {
    Block blockToSet = block;
    int metaToSet = 0;
    switch (type) {
    case CLASSIC:
      metaToSet = ((BlockFluidClassic) block).getMaxRenderHeightMeta();
      break;
    case FINITE:
      metaToSet = ((BlockFluidFinite) block).getMaxRenderHeightMeta();
      break;
    case VANILLA:
      metaToSet = 0;
      break;
    }
    owner.getWorldObj().setBlock(bc.x, bc.y, bc.z, blockToSet, metaToSet, 3);
  }

  private void setVerticalBlock(BlockCoord bc, boolean blockUpdate) {
    Block blockToSet = block;
    int metaToSet = 0;
    switch (type) {
    case CLASSIC:
      metaToSet = 1;
      break;
    case FINITE:
      return;
    case VANILLA:
      metaToSet = 8;
      break;
    }
    owner.getWorldObj().setBlock(bc.x, bc.y, bc.z, blockToSet, metaToSet, blockUpdate ? 3 : 2);
  }

}
