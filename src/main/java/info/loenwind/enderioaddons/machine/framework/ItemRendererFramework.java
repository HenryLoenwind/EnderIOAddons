package info.loenwind.enderioaddons.machine.framework;

import info.loenwind.enderioaddons.render.ItemRendererBase;
import info.loenwind.enderioaddons.render.TickRotator;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;


public class ItemRendererFramework extends ItemRendererBase {

  @Nonnull
  private final RendererFrameworkMachine frameRenderer;

  public ItemRendererFramework(@Nonnull RendererFrameworkMachine frameRenderer) {
    this.frameRenderer = frameRenderer;
  }

  @Override
  protected void setup(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    if (type == ItemRenderType.INVENTORY) {
    } else if (type == ItemRenderType.EQUIPPED) {
      // show face away from character
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      GL11.glRotated(-90, 0, 1, 0);
      GL11.glEnable(GL11.GL_CULL_FACE);
    } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      // show face on left side
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      GL11.glRotated(-90, 0, 1, 0);
      GL11.glEnable(GL11.GL_CULL_FACE);
    } else if (type == ItemRenderType.ENTITY) {
      if (item.isOnItemFrame()) {
        GL11.glRotated(90, 0, 1, 0);
        if (shouldRotateInItemFrame()) {
          TickRotator.getInstance().rotateGl(2);
        }
      }
    }
  }

  @Override
  protected void render(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    RenderUtil.bindBlockTexture();
    frameRenderer.renderInventoryBlock(Block.getBlockFromItem(item.getItem()), item.getItemDamage(), 0, renderBlocks);
    GL11.glDisable(GL11.GL_ALPHA_TEST);
  }

}
