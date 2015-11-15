package info.loenwind.enderioaddons.machine.voidtank;

import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import info.loenwind.enderioaddons.render.FaceRenderer;
import info.loenwind.enderioaddons.render.OverlayRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
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
import crazypants.util.RenderPassHelper;

public class RendererVoidTank implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  /*
   * This block is rendered onion-like in layers.
   * 
   * The outer layer is just the normal block and has the broken tank textures.
   * It is then rendered again as inner layer, with the direction reversed so it
   * is visible from both sides.
   * 
   * The same happens for the unbroken variant, but the normal tank textures are
   * used.
   * 
   * The out-of-control portal core is rendered in 3 passes. First there are 3
   * cubes that have only their left/right (EAST_WEST) sides rendered. Then
   * there are 3 cubes for the up/down sides. And finally there's just one cube
   * for front/back (NORTH_SOUTH), which is just the outer cube; the texture has
   * holes for the bar, so it doesn't overlap the outer shell texture.
   * 
   * Yes, outer layer and NORTH_SOUTH could be merged. But I originally planned
   * to render them on render pass 0 and TESR, respectively. And I don't feel
   * like re-doing the textures again, now that they are both on render pass 1.
   * 
   * The unbroken variant just renders a single block with the vanilla portal
   * texture.
   */

  // *f*ront, *b*ack, *t*op, *b*ottom, in pixels from the side
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
  private static BoundingBox bbu = BoundingBox.UNIT_CUBE.scale(.5, .5, .5); // tamed portal

  private static final VertexRotationFacing xform = new VertexRotationFacing(SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  private static final ForgeDirection[] EAST_WEST = { EAST, WEST };
  private static final ForgeDirection[] NORTH_SOUTH = { NORTH, SOUTH };
  private static final ForgeDirection[] UP_DOWN = { UP, DOWN };

  public static void renderBlockFromTileEntityAt(TileEntity te) {
    boolean isBroken = false;
    if (te instanceof TileVoidTank) {
      TileVoidTank me = (TileVoidTank) te;
      isBroken = me.isBroken();
      xform.setRotation(me.getFacingDir());
    } else {
      xform.setRotation(ForgeDirection.SOUTH);
    }

    if (isBroken) {
      if (RenderPassHelper.getBlockRenderPass() == 0) {
        IIcon[] icons = RenderUtil.getBlockTextures(BlockVoidTank.blockVoidTank, 0);
        FaceRenderer.renderCube(bbi, icons, xform, FaceRenderer.stdBrightnessInside, true);
        FaceRenderer.renderCube(bb0, icons, xform, FaceRenderer.stdBrightness, false);
      } else {
        for (ForgeDirection dir : EAST_WEST) {
          final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 6, 0);
          if (icon1 != null) {
            renderSingleFace(bb1, dir, icon1, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }

          final IIcon icon2 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 12, 0);
          if (icon2 != null) {
            renderSingleFace(bb2, dir, icon2, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }

          final IIcon icon3 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 18, 0);
          if (icon3 != null) {
            renderSingleFace(bb3, dir, icon3, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }
        }

        for (ForgeDirection dir : NORTH_SOUTH) {
          final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 6, 0);
          if (icon1 != null) {
            renderSingleFace(bb0, dir, icon1, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }
        }

        for (ForgeDirection dir : UP_DOWN) {
          final IIcon icon1 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 6, 0);
          if (icon1 != null) {
            renderSingleFace(bb4, dir, icon1, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }

          final IIcon icon2 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 12, 0);
          if (icon2 != null) {
            renderSingleFace(bb5, dir, icon2, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }

          final IIcon icon3 = BlockVoidTank.blockVoidTank.getIcon(dir.ordinal() + 18, 0);
          if (icon3 != null) {
            renderSingleFace(bb6, dir, icon3, 0, 16, 0, 16, xform, FaceRenderer.stdBrightness, false);
          }
        }
      }
    } else {
      if (RenderPassHelper.getBlockRenderPass() == 0) {
        IIcon[] icons = makeBlockTextureList(EnderIO.blockTank.getIcon(SOUTH.ordinal(), 1));
        FaceRenderer.renderCube(bbi, icons, xform, FaceRenderer.stdBrightnessInside, true);
        FaceRenderer.renderCube(bb0, icons, xform, FaceRenderer.stdBrightness, false);
      } else {
        IIcon[] icon4 = RenderUtil.getBlockTextures(Blocks.portal, 0);
        FaceRenderer.renderCube(bbu, icon4, null, RenderUtil.getDefaultPerSideBrightness(), false);
      }
    }
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    if (OverlayRenderer.renderOverlays(world, x, y, z, null, renderer.overrideBlockTexture, BlockVoidTank.blockVoidTank, TileVoidTank.class, true)) {
      return true;
    }
    FaceRenderer.setLightingReference(world, BlockVoidTank.blockVoidTank, x, y, z);
    Tessellator.instance.addTranslation(x, y, z);
    renderBlockFromTileEntityAt(world.getTileEntity(x, y, z));
    Tessellator.instance.addTranslation(-x, -y, -z);
    FaceRenderer.clearLightingReference();
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
