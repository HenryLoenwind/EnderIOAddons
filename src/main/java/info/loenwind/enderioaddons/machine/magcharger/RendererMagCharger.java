package info.loenwind.enderioaddons.machine.magcharger;

import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RendererMagCharger implements ISimpleBlockRenderingHandler {

  private static final double px = 1D / 16D;

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  private static BoundingBox bb0 = BoundingBox.UNIT_CUBE; // outer shell
  private static BoundingBox bbi = makePartialBB(1, 0, 1, 15, 16, 15); // inner shell
  private static BoundingBox bb1 = makePartialBB(1, 5, 1, 15, 14, 15); // inside faces
  private static BoundingBox bb2 = makePartialBB(7, 5, 7, 9, 14, 9); // pole
  private static BoundingBox bb3 = makePartialBB(0, 7, 0, 16, 12, 16); // rim

  private static float[] brightnessPerSide = new float[6];
  private static float[] brightnessPerInSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      brightnessPerInSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
    }
  }

  private static final VertexRotationFacing xform = new VertexRotationFacing(SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    xform.setRotation(SOUTH);

    FaceRenderer.setLightingReference(world, BlockMagCharger.blockMagCharger, x, y, z);

    Tessellator.instance.addTranslation(x, y, z);

    IIcon[] icons = getBlockTextures(world, x, y, z, 4);

    renderBlock(icons);

    Tessellator.instance.addTranslation(-x, -y, -z);

    FaceRenderer.clearLightingReference();

    return true;
  }

  static public void renderBlock(Block block, int meta) {
    xform.setRotation(SOUTH);

    IIcon[] icons = getBlockTextures(block, meta, 4);

    renderBlock(icons);
  }

  private static void renderBlock(IIcon[] icons) {
    IIcon[] icons2 = { icons[6], icons[7], null, null, null, null };
    IIcon[] icons3 = { icons[9], icons[9], null, null, null, null };

    FaceRenderer.renderCube(bb0, icons, xform, brightnessPerSide, false);
    FaceRenderer.renderCube(bb0, icons, xform, brightnessPerSide, true);
    FaceRenderer.renderSkirt(bbi, icons[3], xform, brightnessPerInSide, true);

    FaceRenderer.renderCube(bb1, icons2, xform, brightnessPerInSide, true);
    FaceRenderer.renderCube(bb3, icons3, xform, brightnessPerSide, true);

    FaceRenderer.setupVertices(bb2, xform);

    float minU = icons[8].getInterpolatedU(0);
    float maxU = icons[8].getInterpolatedU(2);
    float minV = icons[8].getMinV();
    float maxV = icons[8].getInterpolatedV(9);
    FaceRenderer.renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerInSide, false);

    minU = maxU;
    maxU = icons[8].getInterpolatedU(4);
    FaceRenderer.renderSingleFace(ForgeDirection.EAST, minU, maxU, minV, maxV, xform, brightnessPerInSide, false);

    minU = maxU;
    maxU = icons[8].getInterpolatedU(6);
    FaceRenderer.renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerInSide, false);

    minU = maxU;
    maxU = icons[8].getInterpolatedU(8);
    FaceRenderer.renderSingleFace(ForgeDirection.WEST, minU, maxU, minV, maxV, xform, brightnessPerInSide, false);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockMagCharger.blockMagCharger.getRenderType();
  }
  
  private static BoundingBox makePartialBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    BoundingBox bb = new BoundingBox(px * minX, px * minY, px * minZ, px * maxX, px * maxY, px * maxZ);
    return bb;
  }

  public static IIcon[] getBlockTextures(IBlockAccess world, int x, int y, int z, int extra) {
    Block block = world.getBlock(x, y, z);
    IIcon[] icons = new IIcon[6 + extra];
    for (int i = 0; i < 6 + extra; i++) {
      icons[i] = block.getIcon(world, x, y, z, i);
    }
    return icons;
  }

  public static IIcon[] getBlockTextures(Block block, int meta, int extra) {
    IIcon[] icons = new IIcon[6 + extra];
    for (int i = 0; i < 6 + extra; i++) {
      icons[i] = block.getIcon(i, meta);
    }
    return icons;
  }

}
