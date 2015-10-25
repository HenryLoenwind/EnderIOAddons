package info.loenwind.enderioaddons.machine.magcharger;

import static info.loenwind.enderioaddons.common.NullHelper.notnullM;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

public class ItemRendererMagCharger implements IItemRenderer {

  public ItemRendererMagCharger() {
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
    @Nonnull
    final ItemStack itemToRender = notnullM(item, "Game state error: Missing item to render");

    GL11.glPushMatrix();
    setup(type);
    render(itemToRender);
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
  }

  private static void render(ItemStack item) {
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.setColorOpaque_F(1, 1, 1);
    RenderUtil.bindBlockTexture();
    RendererMagCharger.renderBlock(BlockMagCharger.blockMagCharger, item.getItemDamage());
    Tessellator.instance.draw();
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

  }
