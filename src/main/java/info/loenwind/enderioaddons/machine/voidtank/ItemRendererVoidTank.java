package info.loenwind.enderioaddons.machine.voidtank;

import static info.loenwind.enderioaddons.common.NullHelper.notnullM;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.util.RenderPassHelper;

public class ItemRendererVoidTank implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    @Nonnull
    final ItemStack itemToRender = notnullM(item, "Game state error: Missing item to render");

    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    setup(type);
    render(itemToRender);
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

  private static void setup(ItemRenderType type) {
    if (type == ItemRenderType.INVENTORY) {
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    } else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glEnable(GL11.GL_CULL_FACE);
    } else if (type == ItemRenderType.ENTITY) {
      //      GL11.glScalef(0.5F, 0.5F, 0.5F);
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
  }

  private static void render(ItemStack item) {
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
