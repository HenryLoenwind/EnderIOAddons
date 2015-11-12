package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RendererAfarm implements ISimpleBlockRenderingHandler {

  private static float[] brightnessPerSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
    }
  }

  private VertXForm xform = new VertXForm();

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    GL11.glDisable(GL11.GL_LIGHTING);
    Tessellator.instance.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    Tessellator.instance.draw();
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    FaceRenderer.setLightingReference(world, BlockAfarm.blockAfarm, x, y, z);

    Tessellator.instance.addTranslation(x, y, z);

    IIcon[] icons = RenderUtil.getBlockTextures(BlockAfarm.blockAfarm, 0);

    // --------------------

    //    IIcon override = renderer.overrideBlockTexture;

    BoundingBox bb = BoundingBox.UNIT_CUBE;
    //    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);
    //   FaceRenderer.renderCube(bb, icons, xform, brightnessPerSide, false);

    Tessellator.instance.draw();
    FaceRenderer.renderCube_alt(bb, icons, xform, false);
    Tessellator.instance.startDrawingQuads();

    //    float scale = 0.7f;
    //    float width = 0.4f;
    //    float trans = (1 - scale) / 2;
    //    bb = BoundingBox.UNIT_CUBE.scale(1, scale, width);
    //    bb = bb.translate(0, -trans, 0);
    //    //    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);
    //    FaceRenderer.renderCube(bb, icons, xform, brightnessPerSide, false);
    //
    //    bb = BoundingBox.UNIT_CUBE.scale(width, scale, 1);
    //    bb = bb.translate(0, -trans, 0);
    //    //    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);
    //    FaceRenderer.renderCube(bb, icons, xform, brightnessPerSide, false);
    //
    //    float topWidth = 0.15f;
    //    bb = BoundingBox.UNIT_CUBE.scale(1, topWidth, 1);
    //    bb = bb.translate(0, 0.3f + topWidth / 2f, 0);
    //    //    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);
    //    FaceRenderer.renderCube(bb, icons, xform, brightnessPerSide, false);
    //    //TranslatedCubeRenderer.instance.getRenderer().setOverrideTexture(null);

    FaceRenderer.clearLightingReference();

    if (world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if (te instanceof TileAfarm && ((TileAfarm) te).isActive()) {
        bb = BoundingBox.UNIT_CUBE.scale(1, 0.08, .4);
        bb = bb.translate(0, 0.1f, 0);
        //        bb = bb.translate(x, y, z);
        Tessellator.instance.setColorOpaque_F(1, 1, 1);
        //        CubeRenderer.render(bb, override != null ? override : Blocks.portal.getBlockTextureFromSide(1));
        FaceRenderer.renderCube(bb, Blocks.portal.getBlockTextureFromSide(1), null, null, false);

        bb = BoundingBox.UNIT_CUBE.scale(.4, 0.08, 1);
        bb = bb.translate(0, 0.1f, 0);
        //        bb = bb.translate(x, y, z);
        Tessellator.instance.setColorOpaque_F(1, 1, 1);
        //        CubeRenderer.render(bb, override != null ? override : Blocks.portal.getBlockTextureFromSide(1));
        FaceRenderer.renderCube(bb, Blocks.portal.getBlockTextureFromSide(1), null, null, false);
      }
    }

    // --------------------

    Tessellator.instance.addTranslation(-x, -y, -z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockAfarm.blockAfarm.getRenderType();
  }

  private static class VertXForm implements VertexTransform {

    public VertXForm() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      if (vec.y > 0.9) {
        double pinch = 0.5;
        vec.x -= 0.5;
        vec.x *= pinch;
        vec.x += 0.5;
        vec.z -= 0.5;
        vec.z *= pinch;
        vec.z += 0.5;
      }

      double scale = 0.8;
      vec.y -= 0.5;
      vec.y *= scale;
      vec.y += (0.5 * scale);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
