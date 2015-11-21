package info.loenwind.enderioaddons.machine.part;

import static info.loenwind.enderioaddons.common.NullHelper.notnullM;
import static info.loenwind.enderioaddons.config.Config.rotationInItemframesEnabled;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;
import info.loenwind.enderioaddons.machine.tcom.RendererTcom;
import info.loenwind.enderioaddons.render.TickRotator;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
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
    final RenderBlocks renderBlocks = notnullM((RenderBlocks) data[0], "Game state error: Missing RenderBlock parameter");
    @Nonnull
    final ItemStack itemToRender = notnullM(item, "Game state error: Missing item to render");

    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    if (item.getItemDamage() == MachinePart.TRAY.ordinal() || item.getItemDamage() == MachinePart.PYLON.ordinal()
        || item.getItemDamage() == MachinePart.PYLONTANK.ordinal()) {
      setup(implExplicit, type, itemToRender, renderBlocks).render(itemToRender, renderBlocks, frameRenderer);
    } else if (MachinePart.values()[item.getItemDamage()].renderAsFrameMachine) {
      setup(implTechne, type, itemToRender, renderBlocks).render(itemToRender, renderBlocks, frameRenderer);
    } else if (MachinePart.values()[item.getItemDamage()].render3d) {
      setup(implBlockAsItem, type, itemToRender, renderBlocks).render(itemToRender, renderBlocks, frameRenderer);
    }
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

  private Impl setup(Impl impl, ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    if(type == ItemRenderType.INVENTORY) {
      impl.setup_Inventory(type, item, renderBlocks);
    } else if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      impl.setup_Equipped(type, item, renderBlocks);
    } else if(type == ItemRenderType.ENTITY) {
      impl.setup_Entity(type, item, renderBlocks);
    } else if (!loggedError) {
      Log.warn("MachinePartRenderer.renderItem: Unsupported render type");
      loggedError = true;
    }
    return impl;
  }

  private static interface Impl {
    void setup_Entity(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks);

    void setup_Equipped(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks);

    void setup_Inventory(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks);

    void render(@Nonnull ItemStack item, @Nonnull RenderBlocks renderBlocks, RendererFrameworkMachine frameRenderer);
  }

  private static class ImplTechne implements Impl {

    @Override
    public void setup_Entity(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      GL11.glRotated(90, 0, 1, 0);
      if (rotationInItemframesEnabled.getBoolean() && item.isOnItemFrame()) {
        TickRotator.getInstance().rotateGl(2);
      }
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      if (!MachinePart.values()[item.getItemDamage()].hasFrame && !MachinePart.values()[item.getItemDamage()].hasTanks) {
        if (MachinePart.values()[item.getItemDamage()].hasSingleTank) {
          GL11.glTranslatef(0.25F, 0.25F, -0.25F);
        } else {
          GL11.glTranslatef(0.25F, -0.25F, -0.25F);
        }
      }
    }

    @Override
    public void setup_Equipped(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      if (!MachinePart.values()[item.getItemDamage()].hasFrame && !MachinePart.values()[item.getItemDamage()].hasTanks) {
        if (MachinePart.values()[item.getItemDamage()].hasSingleTank) {
          GL11.glTranslatef(0.25F, 0.25F, -0.25F);
        } else {
          GL11.glTranslatef(0.25F, -0.25F, -0.25F);
        }
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
          GL11.glTranslatef(0F, 0.25F, 0F);
        }
      }
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public void setup_Inventory(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      if (!MachinePart.values()[item.getItemDamage()].hasFrame && !MachinePart.values()[item.getItemDamage()].hasTanks) {
        //        GL11.glScalef(1.5F, 1.5F, 1.5F);
        if (MachinePart.values()[item.getItemDamage()].hasSingleTank) {
          GL11.glTranslatef(0.25F, 0.25F, -0.25F);
        } else {
          GL11.glTranslatef(0.25F, -0.25F, -0.25F);
        }
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      }
    }

    @Override
    public void render(ItemStack item, RenderBlocks renderBlocks, RendererFrameworkMachine frameRenderer) {
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      RenderUtil.bindBlockTexture();
      frameRenderer.renderInventoryBlock(AbstractBlockFramework.blockDummy, item.getItemDamage() + 16, 0, renderBlocks);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
  }

  private static class ImplBlockAsItem implements Impl {

    @Override
    public void setup_Entity(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      GL11.glRotated(90, 0, 1, 0);
      if (rotationInItemframesEnabled.getBoolean() && item.isOnItemFrame()) {
        TickRotator.getInstance().rotateGl(2);
      }
      GL11.glScalef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public void setup_Equipped(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public void setup_Inventory(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    }

    @Override
    public void render(ItemStack item, RenderBlocks renderBlocks, RendererFrameworkMachine frameRenderer) {
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      renderBlocks.setOverrideBlockTexture(ItemMachinePart.itemMachinePart.getIconFromDamage(item.getItemDamage()));
      renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1.0F);
      renderBlocks.clearOverrideBlockTexture();
      GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

  }

  private static class ImplExplicit implements Impl {

    @Override
    public void setup_Entity(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      GL11.glRotated(90, 0, 1, 0);
      if (rotationInItemframesEnabled.getBoolean() && item.isOnItemFrame()) {
        TickRotator.getInstance().rotateGl(2);
      }
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    @Override
    public void setup_Equipped(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
    }

    @Override
    public void setup_Inventory(ItemRenderType type, ItemStack item, RenderBlocks renderBlocks) {
      GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    @Override
    public void render(ItemStack item, RenderBlocks renderBlocks, RendererFrameworkMachine frameRenderer) {
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      Tessellator.instance.startDrawingQuads();
      Tessellator.instance.setColorOpaque_F(1, 1, 1);
      RenderUtil.bindBlockTexture();
      if (item.getItemDamage() == MachinePart.TRAY.ordinal()) {
        RendererTcom.renderStandaloneTray();
      } else if (item.getItemDamage() == MachinePart.PYLON.ordinal()) {
        RendererTcom.renderStandaloneEnchantmentPylon(false);
      } else if (item.getItemDamage() == MachinePart.PYLONTANK.ordinal()) {
        RendererTcom.renderStandaloneEnchantmentPylon(true);
      }
      Tessellator.instance.draw();
      GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
  }

  private static final Impl implTechne = new ImplTechne();
  private static final Impl implBlockAsItem = new ImplBlockAsItem();
  private static final Impl implExplicit = new ImplExplicit();
}
