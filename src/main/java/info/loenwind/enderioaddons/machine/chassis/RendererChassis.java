package info.loenwind.enderioaddons.machine.chassis;

import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RendererChassis implements ISimpleBlockRenderingHandler {

  private static BoundingBox bbi = BoundingBox.UNIT_CUBE.scale(.99, .99, .99); // inner shell

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    if (renderer.overrideBlockTexture == null) {
      final int blockMetadata = world.getBlockMetadata(x, y, z);
      if (blockMetadata == 0 || blockMetadata >= 14) {
        FaceRenderer.setLightingReference(world, block, x, y, z);

        Tessellator.instance.addTranslation(x, y, z);

        IIcon[] icons = RenderUtil.getBlockTextures(block, blockMetadata);

        FaceRenderer.renderCube(bbi, icons, null, FaceRenderer.stdBrightnessInside, true);

        Tessellator.instance.addTranslation(-x, -y, -z);

        FaceRenderer.clearLightingReference();
      }
    }
    return renderer.renderStandardBlock(block, x, y, z);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockChassis.blockChassis.getRenderType();
  }

}
