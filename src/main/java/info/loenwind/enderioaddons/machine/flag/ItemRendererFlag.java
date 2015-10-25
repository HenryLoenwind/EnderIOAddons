package info.loenwind.enderioaddons.machine.flag;

import static info.loenwind.enderioaddons.common.NullHelper.notnullM;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.obelisk.ObeliskRenderer;

public class ItemRendererFlag implements IItemRenderer {

  private static final ObeliskRenderer obeliskRenderer = new ObeliskRenderer();

  public ItemRendererFlag() {
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
    final RenderBlocks renderBlocks = notnullM((RenderBlocks) data[0], "Game state error: Missing RenderBlock parameter");
    @Nonnull
    final ItemStack itemToRender = notnullM(item, "Game state error: Missing item to render");

    GL11.glPushMatrix();
    setup(type);
    render(itemToRender, renderBlocks);
    GL11.glPopMatrix();
  }

  private static void setup(ItemRenderType type) {
    if(type == ItemRenderType.INVENTORY) {
      GL11.glTranslatef(-0.5F, -0.25F, -0.5F);
    } else if (type == ItemRenderType.EQUIPPED) {
      GL11.glTranslatef(0, 0.25F, 0);
    } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glTranslatef(0, 0.5F, 0);
    } else if(type == ItemRenderType.ENTITY) {
      //      GL11.glScalef(0.5F, 0.5F, 0.5F);
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }
  }

  public static void render(ItemStack item, RenderBlocks renderBlocks) {
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
