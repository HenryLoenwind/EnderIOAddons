package info.loenwind.enderioaddons.machine.voidtank;

import info.loenwind.enderioaddons.render.ItemRendererBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.util.RenderPassHelper;

public class ItemRendererVoidTank extends ItemRendererBase {

  @Override
  protected void setup(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    super.setup(type, item, renderBlocks);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
  }

  @Override
  protected void render(ItemStack item, RenderBlocks renderBlocks) {
    TileVoidTank te = null;
    if(item.stackTagCompound != null) {
      te = new TileVoidTank();
      te.readCommon(item.stackTagCompound);
    }

    RenderUtil.bindBlockTexture();

    Tessellator.instance.setColorOpaque_F(1, 1, 1);
    Tessellator.instance.startDrawingQuads();
    RenderPassHelper.setBlockRenderPass(0);
    RendererVoidTank.renderBlockFromTileEntityAt(te);
    RenderPassHelper.clearBlockRenderPass();
    if (te != null) {
      Tessellator.instance.draw();

      FluidRendererVoidTank.renderTankFluid(te, 0f, 0f, 0f);

      Tessellator.instance.setColorOpaque_F(1, 1, 1);
      Tessellator.instance.startDrawingQuads();
    }
    RenderPassHelper.setBlockRenderPass(1);
    RendererVoidTank.renderBlockFromTileEntityAt(te);
    RenderPassHelper.clearBlockRenderPass();
    Tessellator.instance.draw();
}


}
