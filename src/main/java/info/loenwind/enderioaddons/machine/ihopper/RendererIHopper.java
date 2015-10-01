package info.loenwind.enderioaddons.machine.ihopper;

import static info.loenwind.enderioaddons.common.NullHelper.notnull;
import static info.loenwind.enderioaddons.common.NullHelper.notnullJ;
import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import info.loenwind.enderioaddons.machine.framework.GroupObjectWithIcon;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.Log;
import crazypants.enderio.machine.AbstractMachineEntity;

public class RendererIHopper implements ISimpleBlockRenderingHandler {

  @Nonnull
  private final RendererFrameworkMachine frameRenderer;

  public RendererIHopper(@Nonnull RendererFrameworkMachine frameRenderer) {
    this.frameRenderer = frameRenderer;
    GroupObject controllerActive = new GroupObjectWithIcon(frameRenderer.getControllerPart(5), BlockIHopper.getBlock());
    GroupObject controllerPassive = new GroupObjectWithIcon(frameRenderer.getControllerPart(6), BlockIHopper.getBlock());

    frameRenderer.registerController(BlockIHopper.getBlock().getControllerModelName(),
        controllerActive, controllerPassive);
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    frameRenderer.renderInventoryBlock(block, metadata, modelId, renderer);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileEntity te = world != null ? world.getTileEntity(x, y, z) : null;
    IFrameworkMachine frameworkMachine = te instanceof IFrameworkMachine ? (IFrameworkMachine) te : null;
    AbstractMachineEntity machineEntity = te instanceof AbstractMachineEntity ? (AbstractMachineEntity) te : null;

    if (frameworkMachine != null && machineEntity != null) {
      float[] brightnessPerSide = new float[6];
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      }

      for (TankSlot tankSlot : TankSlot.values()) {
        tankSlot = notnullJ(tankSlot, "enum.values()[i]");
        if (tankSlot != TankSlot.FRONT_LEFT) {
          renderSubBlock(x, y, z, machineEntity, brightnessPerSide, tankSlot);
        }
      }
    }

    return frameRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockIHopper.blockIHopper.getRenderType();
  }

  private final static ForgeDirection[] AROUND = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };

  private void renderSubBlock(int x, int y, int z, @Nonnull AbstractMachineEntity te, float[] brightnessPerSide, @Nonnull TankSlot tankSlot) {

    IIcon icon_outside = BlockHopper.getHopperIcon("hopper_outside");
    IIcon icon_inside = BlockHopper.getHopperIcon("hopper_inside");
    IIcon icon_top = Blocks.hopper.getIcon(1, 0);

    int[] pos = frameRenderer.translateToSlotPosition(notnull(te.getFacingDir(), "Internal state error: Block is not facing any direction"), tankSlot);

    // top box
    Log.info("BB1");
    BoundingBox bb1 = makePartialBBofSlot(0, 10, 0, 16, 16, 16, pos, x, y, z);
    renderSingleFace(bb1, ForgeDirection.UP, icon_top, 0, 16, 0, 16, null, brightnessPerSide, false);
    for (ForgeDirection dir : AROUND) {
      renderSingleFace(bb1, dir, icon_outside, 0, 16, 10, 16, null, brightnessPerSide, false);
    }
    renderSingleFace(bb1, ForgeDirection.DOWN, icon_outside, 0, 16, 0, 16, null, brightnessPerSide, false);

    // inside
    BoundingBox bb2 = makePartialBBofSlot(2, 10, 2, 14, 16, 14, pos, x, y, z);
    for (ForgeDirection dir : AROUND) {
      renderSingleFace(bb2, dir, icon_outside, 2, 14, 10, 16, null, brightnessPerSide, true);
    }
    renderSingleFace(bb2, ForgeDirection.DOWN, icon_inside, 2, 14, 2, 14, null, brightnessPerSide, true);

    // center box
    BoundingBox bb3 = makePartialBBofSlot(4, 4, 4, 12, 10, 12, pos, x, y, z);
    for (ForgeDirection dir : AROUND) {
      renderSingleFace(bb3, dir, icon_outside, 4, 12, 4, 10, null, brightnessPerSide, false);
    }
    renderSingleFace(bb3, ForgeDirection.DOWN, icon_outside, 4, 12, 4, 12, null, brightnessPerSide, false);

    // connector
    ForgeDirection cdir = getConnectorDirection(notnull(te.getFacingDir(), "Internal state error: Block is not facing any direction"), tankSlot);
    int x0, x1, y0, y1, z0, z1;
    switch (cdir) {
    case NORTH:
      x0 = 6;
      y0 = 4;
      z0 = 12;
      x1 = 10;
      y1 = 8;
      z1 = 16;
      break;
    case SOUTH:
      x0 = 6;
      y0 = 4;
      z0 = 0;
      x1 = 10;
      y1 = 8;
      z1 = 4;
      break;
    case EAST:
      x0 = 12;
      y0 = 4;
      z0 = 6;
      x1 = 16;
      y1 = 8;
      z1 = 10;
      break;
    case WEST:
      x0 = 0;
      y0 = 4;
      z0 = 6;
      x1 = 4;
      y1 = 8;
      z1 = 10;
      break;
    case DOWN:
      x0 = 6;
      y0 = 0;
      z0 = 6;
      x1 = 10;
      y1 = 4;
      z1 = 10;
      break;
    default:
      return;
    }
    BoundingBox bb4 = makePartialBBofSlot(x0, y0, z0, x1, y1, z1, pos, x, y, z);
    renderSingleFace(bb4, ForgeDirection.UP, icon_outside, x0, x1, z0, z1, null, brightnessPerSide, false);
    renderSingleFace(bb4, ForgeDirection.NORTH, icon_outside, x0, x1, y0, y1, null, brightnessPerSide, false);
    renderSingleFace(bb4, ForgeDirection.SOUTH, icon_outside, x0, x1, y0, y1, null, brightnessPerSide, false);
    renderSingleFace(bb4, ForgeDirection.EAST, icon_outside, z0, z1, y0, y1, null, brightnessPerSide, false);
    renderSingleFace(bb4, ForgeDirection.WEST, icon_outside, z0, z1, y0, y1, null, brightnessPerSide, false);
    renderSingleFace(bb4, ForgeDirection.DOWN, icon_outside, x0, x1, z0, z1, null, brightnessPerSide, false);

  }

  private static final double px = 1D / 16D;
  private static BoundingBox makePartialBBofSlot(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int[] pos, int x, int y, int z) {
    BoundingBox bb = new BoundingBox(px * minX, px * minY, px * minZ, px * maxX, px * maxY, px * maxZ);
    bb = centerscale(bb, px * 6D, px * 6D, px * 6D);
    bb = bb.translate(pos[0] * 4f / 16f, 4f / 16f, pos[1] * 4f / 16f);
    bb = bb.translate(x, y, z);
    return bb;
  }

  private static BoundingBox centerscale(BoundingBox bb, double x, double y, double z) {
    return new BoundingBox((bb.minX - 0.5d) * x + 0.5d, (bb.minY - 0.5d) * y + 0.5d, (bb.minZ - 0.5d) * z + 0.5d, (bb.maxX - 0.5d) * x + 0.5d, (bb.maxY - 0.5d)
        * y + 0.5d, (bb.maxZ - 0.5d) * z + 0.5d);
  }

  @Nonnull
  private static final ForgeDirection[] positions = { SOUTH, WEST, NORTH, EAST };

  private static ForgeDirection getConnectorDirection(@Nonnull ForgeDirection dir, @Nonnull TankSlot tankSlot) {
    switch (dir) {
    case NORTH:
      return positions[(2 + tankSlot.ordinal()) & 3];
    case SOUTH:
      return positions[(0 + tankSlot.ordinal()) & 3];
    case WEST:
      return positions[(3 + tankSlot.ordinal()) & 3];
    case EAST:
      return positions[(1 + tankSlot.ordinal()) & 3];
    default:
      break;
    }
    return positions[0];
  }

}
