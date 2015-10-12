package info.loenwind.enderioaddons.machine.niard;

import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
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
    IIcon[] icons = RenderUtil.getBlockTextures(BlockNiard.blockNiard, 0);
    FaceRenderer.renderCube(bb, icons, null, brightnessPerSide, true);

    BoundingBox bb2 = BoundingBox.UNIT_CUBE.scale(13 / 16f, 13 / 16f, 13 / 16f).translate(x, y, z);
    icons = RenderUtil.getBlockTextures(BlockNiard.blockNiard, 1);
    FaceRenderer.renderCube(bb2, icons, null, brightnessPerSide, false);
    FaceRenderer.renderCube(bb2, icons, null, brightnessPerSide, true);

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
