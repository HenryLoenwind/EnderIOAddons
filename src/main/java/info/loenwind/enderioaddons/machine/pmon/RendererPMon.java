package info.loenwind.enderioaddons.machine.pmon;

import static info.loenwind.enderioaddons.config.Config.pMonEnableDynamicTextures;
import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RendererPMon implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  private static BoundingBox bb0 = BoundingBox.UNIT_CUBE; // outer shell
  private static BoundingBox bb1 = BoundingBox.UNIT_CUBE.translate(0f, 0f, -.1f); // screen
  private static BoundingBox bbi = BoundingBox.UNIT_CUBE.scale(.99, .99, .99); // inner shell

  private static float[] brightnessPerSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
    }
  }

  private static final VertexRotationFacing xform = new VertexRotationFacing(ForgeDirection.SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  public static void renderTileEntityAt(TilePMon te) {
    if (te != null) {
      xform.setRotation(te.getFacingDir());
      te.bindTexture();
      FaceRenderer.setupVertices(bb1, xform);
      GL11.glColor4f(1F, 1F, 1F, 1F);
      FaceRenderer.renderSingleFace(ForgeDirection.SOUTH, 0f, 1f, 0f, 1f, xform, brightnessPerSide, false);
    } else {
      xform.setRotation(ForgeDirection.SOUTH);
      RenderUtil.bindBlockTexture();
      IIcon[] icons = RenderUtil.getBlockTextures(BlockPMon.blockPMon, 0);
      FaceRenderer.renderSingleFace(bb1, ForgeDirection.SOUTH, icons, xform, brightnessPerSide, false);
    }
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TilePMon me = null;
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TilePMon) {
      me = (TilePMon) te;
      xform.setRotation(me.getFacingDir());
    }

    FaceRenderer.setLightingReference(world, BlockPMon.blockPMon, x, y, z);

    Tessellator.instance.addTranslation(x, y, z);

    IIcon[] icons = RenderUtil.getBlockTextures(BlockPMon.blockPMon, 0);
    if (me != null && pMonEnableDynamicTextures.getBoolean()) {
      icons[ForgeDirection.SOUTH.ordinal()] = BlockPMon.blockPMon.getIcon(ForgeDirection.SOUTH.ordinal() + 6, 0);
    }
    FaceRenderer.renderCube(bb0, icons, xform, brightnessPerSide, false);

    FaceRenderer.renderCube(bbi, BlockPMon.blockPMon.getIcon(ForgeDirection.UP.ordinal() + 6, 0), xform, brightnessPerSide, true);

    Tessellator.instance.addTranslation(-x, -y, -z);

    FaceRenderer.clearLightingReference();

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockPMon.blockPMon.getRenderType();
  }
  
}
