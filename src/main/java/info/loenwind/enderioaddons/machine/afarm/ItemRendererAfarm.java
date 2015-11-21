package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.render.ItemRendererBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

public class ItemRendererAfarm extends ItemRendererBase {

  @Override
  protected void setup(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    super.setup(type, item, renderBlocks);
    if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glTranslatef(0F, 0.2F, 0F);
    }
  }

  @Override
  protected void render(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    RenderHelper.enableStandardItemLighting();
    GL11.glShadeModel(GL11.GL_SMOOTH);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    RenderUtil.bindBlockTexture();
    RendererAfarm.renderBlock(true);
    RenderHelper.disableStandardItemLighting();
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

  }
