package info.loenwind.enderioaddons.machine.niard;

import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRendererNiard implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    float[] brightnessPerSide = new float[6];
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
    }

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(.99, .99, .99).translate(x, y, z);
    BoundingBox bb2 = BoundingBox.UNIT_CUBE.scale(13 / 16f, 13 / 16f, 13 / 16f).translate(x, y, z);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      renderSingleFace(bb, dir, BlockNiard.blockNiard.getIcon(dir.ordinal(), 0), 0, 16, 0, 16, null, brightnessPerSide, true);
      renderSingleFace(bb2, dir, BlockNiard.blockNiard.getIcon(dir.ordinal(), 1), 0, 16, 0, 16, null, brightnessPerSide, false);
      renderSingleFace(bb2, dir, BlockNiard.blockNiard.getIcon(dir.ordinal(), 1), 0, 16, 0, 16, null, brightnessPerSide, true);
    }

    renderer.renderStandardBlock(BlockNiard.blockNiard, x, y, z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockNiard.blockNiard.getRenderType();
  }
  
}
