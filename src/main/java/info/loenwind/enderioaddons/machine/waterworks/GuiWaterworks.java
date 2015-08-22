package info.loenwind.enderioaddons.machine.waterworks;

import java.awt.Rectangle;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiWaterworks extends GuiPoweredMachineBase<TileWaterworks> {

  private static final String GUI_TEXTURE = "enderioaddons:textures/gui/waterworks.png";

  public GuiWaterworks(InventoryPlayer par1InventoryPlayer, TileWaterworks te) {
    super(te, new ContainerWaterworks(par1InventoryPlayer, te));

    addToolTip(new GuiToolTip(new Rectangle(30, 9, 15, 47), "") {
      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("tank.tank");
        if (getTileEntity().tank1.getFluid() != null) {
          heading += ": " + getTileEntity().tank1.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().tank1));
      }
    });

    addToolTip(new GuiToolTip(new Rectangle(79, 9, 15, 47), "") {
      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("tank.tank");
        if (getTileEntity().tank2.getFluid() != null) {
          heading += ": " + getTileEntity().tank2.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().tank2));
      }
    });

  }

  @Override
  protected int getPowerHeight() {
    return 47;
  }

  @Override
  protected int getPowerY() {
    return 9;
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }


  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(GUI_TEXTURE);
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    if (shouldRenderProgress()) {
      int scaled = getProgressScaled(14) + 1;
      drawTexturedModalRect(guiLeft + 55, guiTop + 61 + 14 - scaled, 176, 14 - scaled, 14, scaled);
    }

    if (getTileEntity().tank1.getFluid() != null) {
      RenderUtil.renderGuiTank(getTileEntity().tank1, guiLeft + 30, guiTop + 9, zLevel, 15, 47);
    }
    if (getTileEntity().tank2.getFluid() != null) {
      RenderUtil.renderGuiTank(getTileEntity().tank2, guiLeft + 79, guiTop + 9, zLevel, 15, 47);
    }

    if (getTileEntity().progress_in != null) {
      RenderUtil.bindBlockTexture();
      GL11.glEnable(GL11.GL_BLEND);
      float progress0 = getTileEntity().getProgress();
      float progress = MathHelper.clamp_float(progress0 * 1.3f - 0.15f, 0, 1);
      int cfactor = 10; // TODO
      int hmin = 26 * cfactor / 100;
      int h = (int) (hmin + (1f - progress) * (26 - hmin));
      int offset = 26 - h;
      GL11.glColor4f(1, 1, 1, 0.75f * (1f - progress0));
      drawTexturedModelRectFromIcon(guiLeft + 50, guiTop + 32 + offset, getTileEntity().progress_in.getStillIcon(), 24, h);
      if (getTileEntity().progress_out != null) {
        GL11.glColor4f(1, 1, 1, 0.75f * progress0);
        drawTexturedModelRectFromIcon(guiLeft + 50, guiTop + 32 + offset, getTileEntity().progress_out.getStillIcon(), 24, h);
      }
      GL11.glDisable(GL11.GL_BLEND);
    }

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(GUI_TEXTURE);
    drawTexturedModalRect(guiLeft + 50, guiTop + 32, 208, 6, 24, 26); // boiler
    drawTexturedModalRect(guiLeft + 30, guiTop + 9, 197, 41, 15, 47); // left tank
    drawTexturedModalRect(guiLeft + 79, guiTop + 9, 215, 41, 15, 47); // right tank
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.bindBlockTexture();
  }

}
