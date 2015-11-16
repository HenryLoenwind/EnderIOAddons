package info.loenwind.enderioaddons.machine.afarm;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

public class ItemRendererAfarm implements IItemRenderer {

  public ItemRendererAfarm() {
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    setup(type);
    render();
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

  private static void setup(ItemRenderType type) {
    if (type == ItemRenderType.INVENTORY) {
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    } else if (type == ItemRenderType.EQUIPPED) {
      GL11.glEnable(GL11.GL_CULL_FACE);
    } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glTranslatef(0F, 0.2F, 0F);
    } else if (type == ItemRenderType.ENTITY) {
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }
  }

  private static void render() {
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
