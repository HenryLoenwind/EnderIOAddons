package info.loenwind.enderioaddons.machine.drain;

import info.loenwind.enderioaddons.render.FaceRenderer;
import info.loenwind.enderioaddons.render.OverlayRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class DrainBlockRenderer implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  private static final BoundingBox bb_inside = BoundingBox.UNIT_CUBE.scale(0.98, 0.98, 0.98);
  private static final BoundingBox bb_innertop = BoundingBox.UNIT_CUBE.scale(0.98, 0.98, 0.98).translate(0f, 0.5f, 0f);

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    if (OverlayRenderer.renderOverlays(world, x, y, z, null, renderer.overrideBlockTexture, BlockDrain.blockDrain, TileDrain.class, true)) {
      return true;
    }

    final IIcon icon_sides = BlockDrain.blockDrain.getIcon(2, 0);
    final IIcon icon_bottom = BlockDrain.blockDrain.getIcon(0, 0);

    Tessellator.instance.addTranslation(x, y, z);

    FaceRenderer.setLightingReference(world, BlockDrain.blockDrain, x, y, z);

    FaceRenderer.renderSkirt(bb_inside, icon_sides, null, FaceRenderer.stdBrightnessInside, true);
    FaceRenderer.renderSingleFace(bb_innertop, ForgeDirection.DOWN, icon_bottom, null, FaceRenderer.stdBrightnessInside, true);

    FaceRenderer.clearLightingReference();

    Tessellator.instance.addTranslation(-x, -y, -z);

    renderer.renderStandardBlock(BlockDrain.blockDrain, x, y, z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockDrain.blockDrain.getRenderType();
  }
  
}
