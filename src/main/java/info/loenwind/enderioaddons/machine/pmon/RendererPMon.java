package info.loenwind.enderioaddons.machine.pmon;

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
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RendererPMon implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  private static BoundingBox bb0 = BoundingBox.UNIT_CUBE; // outer shell

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

  public static void renderTileEntityAt(TilePMon te, IBlockAccess world, int x, int y, int z) {
    //    FaceRenderer.setLightingReference(world, BlockPMon.blockPMon, x, y, z);
    if (te == null) {
      xform.setRotation(te.getFacingDir());
      te.bindTexture();
      FaceRenderer.setupVertices(bb0, xform);
      GL11.glColor4f(1F, 1F, 1F, 1F);
      FaceRenderer.renderSingleFace(ForgeDirection.SOUTH, 0f, 1f, 0f, 1f, xform, brightnessPerSide, false);
    } else {
      xform.setRotation(te.getFacingDir());
      //      xform.setRotation(ForgeDirection.SOUTH);
      RenderUtil.bindBlockTexture();
      IIcon[] icons = RenderUtil.getBlockTextures(BlockPMon.blockPMon, 0);
      FaceRenderer.renderSingleFace(bb0, ForgeDirection.SOUTH, icons, xform, brightnessPerSide, false);
    }
    //    FaceRenderer.clearLightingReference();
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
    if (me != null) {
      icons[ForgeDirection.SOUTH.ordinal()] = IconUtil.blankTexture;
    }
    //FaceRenderer.renderCube(bb0, icons, xform, brightnessPerSide, false);

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
