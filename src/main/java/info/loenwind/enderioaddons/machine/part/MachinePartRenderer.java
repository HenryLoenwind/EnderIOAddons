package info.loenwind.enderioaddons.machine.part;

import static info.loenwind.enderioaddons.common.NullHelper.notnull;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.Log;

public class MachinePartRenderer implements IItemRenderer {

  private boolean loggedError = false;
  @Nonnull
  private final RendererFrameworkMachine frameRenderer;

  public MachinePartRenderer(@Nonnull RendererFrameworkMachine frameRenderer) {
    this.frameRenderer = frameRenderer;
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    if (item != null && MachinePart.values()[item.getItemDamage()].render3d) {
      return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY
          || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    } else {
      return false;
    }
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    @Nonnull
    final RenderBlocks renderBlocks = notnull((RenderBlocks) data[0], "Game state error: Missing RenderBlock parameter");
    @Nonnull
    final ItemStack itemToRender = notnull(item, "Game state error: Missing item to render");
    if(type == ItemRenderType.INVENTORY) {
      renderToInventory(itemToRender, renderBlocks);
    } else if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      renderEquipped(itemToRender, renderBlocks);
    } else if(type == ItemRenderType.ENTITY) {
      renderEntity(itemToRender, renderBlocks);
    } else if (!loggedError) {
      Log.warn("MachinePartRenderer.renderItem: Unsupported render type");
      loggedError = true;
    }
  }

  private void renderEntity(@Nonnull ItemStack item, @Nonnull RenderBlocks renderBlocks) {
    GL11.glPushMatrix();
    GL11.glScalef(0.5f, 0.5f, 0.5f);
    renderToInventory(item, renderBlocks);
    GL11.glPopMatrix();
  }

  private void renderEquipped(@Nonnull ItemStack item, @Nonnull RenderBlocks renderBlocks) {
    GL11.glPushMatrix();
    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    renderToInventory(item, renderBlocks);
    GL11.glPopMatrix();
  }

  private void renderToInventory(@Nonnull ItemStack item, @Nonnull RenderBlocks renderBlocks) {
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    if (MachinePart.values()[item.getItemDamage()].renderAsFrameMachine) {
      RenderUtil.bindBlockTexture();
      frameRenderer.renderInventoryBlock(AbstractBlockFramework.blockDummy, item.getItemDamage() + 16, 0, renderBlocks);
    } else if (MachinePart.values()[item.getItemDamage()].render3d) {
      renderBlocks.setOverrideBlockTexture(ItemMachinePart.itemMachinePart.getIconFromDamage(item.getItemDamage()));
      renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1.0F);
      renderBlocks.clearOverrideBlockTexture();
    }
    GL11.glDisable(GL11.GL_ALPHA_TEST);
  }
}
