package info.loenwind.enderioaddons.machine.voidtank;

import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import static info.loenwind.enderioaddons.render.FaceRenderer.sideAndFacingToSpriteOffset;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.machine.AbstractMachineEntity;

public class BlockRendererVoidTank implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  private static BoundingBox bb0; // outer shell
  private static BoundingBox bbi; // inner shell
  private static BoundingBox bb1; // layer 1
  private static BoundingBox bb2; // layer 2
  private static BoundingBox bb3; // layer 3

  private static float[] brightnessPerSide = new float[6];
  private static float[] brightnessPerInSide = new float[6];

  private static final VertexRotationFacing xform = new VertexRotationFacing(ForgeDirection.SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  public static void renderTileEntityAt(AbstractMachineEntity te) {

    short facing = te == null ? 3 : te.facing;
    xform.setRotation(te == null ? ForgeDirection.SOUTH : te.getFacingDir());
    facing = 3;

    if (!setupBBs(0, 0, 0, facing)) {
      return;
    }

    // 0=DOWN, 1=UP, 2=NORTH, 3=SOUTH (neutral), 4=WEST, 5=EAST
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      // 0=top, 1=bottom, 2=back, 3=front, 4=side1, 5=side2
      int sideToRender = sideAndFacingToSpriteOffset[dir.ordinal()][facing];

      if (sideToRender == 4 || sideToRender == 5) {
        final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(sideToRender + 6, 0);
        if (icon1 != null) {
          renderSingleFace(bb1, dir, icon1, 0, 16, 0, 16, xform, brightnessPerSide, false);
        }

        final IIcon icon2 = BlockVoidTank.blockVoidTank.getIcon(sideToRender + 12, 0);
        if (icon2 != null) {
          renderSingleFace(bb2, dir, icon2, 0, 16, 0, 16, xform, brightnessPerSide, false);
        }

        final IIcon icon3 = BlockVoidTank.blockVoidTank.getIcon(sideToRender + 18, 0);
        if (icon3 != null) {
          renderSingleFace(bb3, dir, icon3, 0, 16, 0, 16, xform, brightnessPerSide, false);
        }
      } else if (sideToRender == 2 || sideToRender == 3) {
        final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(sideToRender + 6, 0);
        if (icon1 != null) {
          renderSingleFace(bb0, dir, icon1, 0, 16, 0, 16, xform, brightnessPerSide, false);
        }
      }

    }

    //    renderer.renderStandardBlock(BlockVoidTank.blockVoidTank, x, y, z);

  }

  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      brightnessPerInSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
    }
  }

  private static final int f1 = 0, f2 = 1, f3 = 0, b1 = 7, b2 = 8, b3 = 9;

  private static boolean setupBBs(int x, int y, int z, int facing) {
    bb0 = BoundingBox.UNIT_CUBE.translate(x, y, z); // outer shell
    bbi = BoundingBox.UNIT_CUBE.scale(.99, .99, .99).translate(x, y, z); // inner shell

    if (facing == 2) {
      bb1 = makePartialBB(f1, 0, 0, 16 - b1, 16, 16).translate(x, y, z);
      bb2 = makePartialBB(f2, 0, 0, 16 - b2, 16, 16).translate(x, y, z);
      bb3 = makePartialBB(f3, 0, 0, 16 - b3, 16, 16).translate(x, y, z);
    } else if (facing == 3) {
      bb1 = makePartialBB(b1, 0, 0, 16 - f1, 16, 16).translate(x, y, z);
      bb2 = makePartialBB(b2, 0, 0, 16 - f2, 16, 16).translate(x, y, z);
      bb3 = makePartialBB(b3, 0, 0, 16 - f3, 16, 16).translate(x, y, z);
    } else if (facing == 5) {
      bb1 = makePartialBB(0, 0, f1, 16, 16, 16 - b1).translate(x, y, z);
      bb2 = makePartialBB(0, 0, f2, 16, 16, 16 - b2).translate(x, y, z);
      bb3 = makePartialBB(0, 0, f3, 16, 16, 16 - b3).translate(x, y, z);
    } else if (facing == 4) {
      bb1 = makePartialBB(0, 0, b1, 16, 16, 16 - f1).translate(x, y, z);
      bb2 = makePartialBB(0, 0, b2, 16, 16, 16 - f2).translate(x, y, z);
      bb3 = makePartialBB(0, 0, b3, 16, 16, 16 - f3).translate(x, y, z);
    } else {
      return false;
    }

    return true;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileEntity te = world.getTileEntity(x, y, z);
    int facing = 3;
    if (te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      facing = me.facing;
      xform.setRotation(me.getFacingDir());
      facing = 3;
    }

    Tessellator.instance.addTranslation(x, y, z);

    if (!setupBBs(0, 0, 0, facing)) {
      //    if (!setupBBs(x, y, z, facing)) {
      return false;
    }

    // 0=DOWN, 1=UP, 2=NORTH, 3=SOUTH (neutral), 4=WEST, 5=EAST
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      // 0=top, 1=bottom, 2=back, 3=front, 4=side1, 5=side2
      int sideToRender = sideAndFacingToSpriteOffset[dir.ordinal()][facing];

      final IIcon icon0 = BlockVoidTank.blockVoidTank.getIcon(sideToRender, 0);
      if (icon0 != null) {
        renderSingleFace(bbi, dir, icon0, 0, 16, 0, 16, xform, brightnessPerInSide, true);
        renderSingleFace(bb0, dir, icon0, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }

    }

    Tessellator.instance.addTranslation(-x, -y, -z);

    //    renderer.renderStandardBlock(BlockVoidTank.blockVoidTank, x, y, z);

    return true;
  }


  private static final double px = 1D / 16D;

  private static BoundingBox makePartialBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    BoundingBox bb = new BoundingBox(px * minX, px * minY, px * minZ, px * maxX, px * maxY, px * maxZ);
    return bb;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockVoidTank.blockVoidTank.getRenderType();
  }
  
}
