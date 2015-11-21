package info.loenwind.enderioaddons.machine.flag;

import info.loenwind.enderioaddons.render.ItemRendererBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.obelisk.ObeliskRenderer;

public class ItemRendererFlag extends ItemRendererBase {

  private static final ObeliskRenderer obeliskRenderer = new ObeliskRenderer();

  @Override
  protected void setup(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    super.setup(type, item, renderBlocks);
    if (type == ItemRenderType.INVENTORY) {
      GL11.glTranslatef(0F, 0.25F, 0);
    } else if (type == ItemRenderType.EQUIPPED) {
      GL11.glTranslatef(0, 0.25F, 0);
    } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glTranslatef(0, 0.5F, 0);
    }
  }

  @Override
  protected void render(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.setColorOpaque_F(1, 1, 1);
    RenderUtil.bindBlockTexture();

    BlockFlag.fakeMeta = item.getItemDamage() != 0;
    obeliskRenderer.renderWorldBlock(null, 0, 0, 0, BlockFlag.blockFlag, 0, renderBlocks);
    BlockFlag.fakeMeta = false;

    Tessellator.instance.draw();
    GL11.glDisable(GL11.GL_ALPHA_TEST);
  }

}
