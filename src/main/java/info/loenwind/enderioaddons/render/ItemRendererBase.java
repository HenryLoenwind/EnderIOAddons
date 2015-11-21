package info.loenwind.enderioaddons.render;

import static info.loenwind.enderioaddons.common.NullHelper.notnullM;
import static info.loenwind.enderioaddons.config.Config.rotationInItemframesEnabled;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public abstract class ItemRendererBase implements IItemRenderer {

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
    final RenderBlocks renderBlocks = notnullM((RenderBlocks) data[0], "Game state error: Missing RenderBlock parameter");

    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    setup(type, item, renderBlocks);
    render(item, renderBlocks);
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

  protected void setup(ItemRenderType type, ItemStack item, @SuppressWarnings("unused") RenderBlocks renderBlocks) {
    if (type == ItemRenderType.INVENTORY) {
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    } else if (type == ItemRenderType.EQUIPPED) {
      // show face away from character
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      GL11.glRotated(-90, 0, 1, 0);
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
      GL11.glEnable(GL11.GL_CULL_FACE);
    } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      // show face on left side
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      GL11.glRotated(-90, 0, 1, 0);
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
      GL11.glEnable(GL11.GL_CULL_FACE);
    } else if (type == ItemRenderType.ENTITY) {
      if (item.isOnItemFrame()) {
        GL11.glRotated(90, 0, 1, 0);
        if (shouldRotateInItemFrame()) {
          TickRotator.getInstance().rotateGl(2);
        }
      }
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }
  }

  @SuppressWarnings("static-method")
  protected boolean shouldRotateInItemFrame() {
    return rotationInItemframesEnabled.getBoolean();
  }

  protected abstract void render(ItemStack item, RenderBlocks renderBlocks);

}