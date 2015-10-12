package info.loenwind.enderioaddons.machine.voidtank;

import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import info.loenwind.enderioaddons.render.FaceRenderer;
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
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineEntity;

public class RendererVoidTank implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  private static final int f1 = 0, f2 = 1, f3 = 0, b1 = 7, b2 = 8, b3 = 9;
  private static final int t1 = 0, t2 = 1, t3 = 0, d1 = 0, d2 = 1, d3 = 2;

  private static BoundingBox bb0 = BoundingBox.UNIT_CUBE; // outer shell
  private static BoundingBox bbi = BoundingBox.UNIT_CUBE.scale(.99, .99, .99); // inner shell
  private static BoundingBox bb1 = makePartialBB(b1, 0, 0, 16 - f1, 16, 16); // left/right layer 1
  private static BoundingBox bb2 = makePartialBB(b2, 0, 0, 16 - f2, 16, 16); // left/right layer 2
  private static BoundingBox bb3 = makePartialBB(b3, 0, 0, 16 - f3, 16, 16); // left/right layer 3
  private static BoundingBox bb4 = makePartialBB(0, d1, 0, 16, 16 - t1, 16); // top/bottom layer 1
  private static BoundingBox bb5 = makePartialBB(0, d2, 0, 16, 16 - t2, 16); // top/bottom layer 2
  private static BoundingBox bb6 = makePartialBB(0, d3, 0, 16, 16 - t3, 16); // top/bottom layer 3

  private static float[] brightnessPerSide = new float[6];
  private static float[] brightnessPerInSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      brightnessPerInSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
    }
  }

  private static final VertexRotationFacing xform = new VertexRotationFacing(ForgeDirection.SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  private static final ForgeDirection[] EAST_WEST = { ForgeDirection.EAST, ForgeDirection.WEST };
  private static final ForgeDirection[] NORTH_SOUTH = { ForgeDirection.NORTH, ForgeDirection.SOUTH };
  private static final ForgeDirection[] UP_DOWN = { ForgeDirection.UP, ForgeDirection.DOWN };

  public static void renderTileEntityAt(AbstractMachineEntity te) {

    xform.setRotation(te == null ? ForgeDirection.SOUTH : te.getFacingDir());

    for (ForgeDirection dir : EAST_WEST) {
      final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 6, 0);
      if (icon1 != null) {
        renderSingleFace(bb1, dir, icon1, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }

      final IIcon icon2 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 12, 0);
      if (icon2 != null) {
        renderSingleFace(bb2, dir, icon2, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }

      final IIcon icon3 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 18, 0);
      if (icon3 != null) {
        renderSingleFace(bb3, dir, icon3, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }
    }

    for (ForgeDirection dir : NORTH_SOUTH) {
      final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 6, 0);
      if (icon1 != null) {
        renderSingleFace(bb0, dir, icon1, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }
    }

    for (ForgeDirection dir : UP_DOWN) {
      final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 6, 0);
      if (icon1 != null) {
        renderSingleFace(bb4, dir, icon1, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }

      final IIcon icon2 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 12, 0);
      if (icon2 != null) {
        renderSingleFace(bb5, dir, icon2, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }

      final IIcon icon3 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 18, 0);
      if (icon3 != null) {
        renderSingleFace(bb6, dir, icon3, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }
    }

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    boolean isBroken = false;
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileVoidTank) {
      TileVoidTank me = (TileVoidTank) te;
      isBroken = me.isBroken();
      xform.setRotation(me.getFacingDir());
    }

    FaceRenderer.setLightingReference(world, BlockVoidTank.blockVoidTank, x, y, z);

    Tessellator.instance.addTranslation(x, y, z);

    IIcon[] icons = isBroken ? RenderUtil.getBlockTextures(BlockVoidTank.blockVoidTank, 0) : makeBlockTextureList(EnderIO.blockTank.getIcon(3, 1));
    FaceRenderer.renderCube(bbi, icons, xform, brightnessPerInSide, true);
    FaceRenderer.renderCube(bb0, icons, xform, brightnessPerSide, false);

    Tessellator.instance.addTranslation(-x, -y, -z);

    FaceRenderer.clearLightingReference();
    //    renderer.renderStandardBlock(BlockVoidTank.blockVoidTank, x, y, z);

    return true;
  }

  public static IIcon[] makeBlockTextureList(IIcon icon) {
    IIcon[] icons = new IIcon[6];
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      icons[dir.ordinal()] = icon;
    }
    return icons;
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
