package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.Config;

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

  private static final String GUI_TEXTURE = EnderIOAddons.DOMAIN + ":textures/gui/waterworks.png";

  public GuiWaterworks(InventoryPlayer par1InventoryPlayer, TileWaterworks te) {
    super(te, new ContainerWaterworks(par1InventoryPlayer, te));

    addToolTip(new GuiToolTip(new Rectangle(30, 9, 15, 47), "") {
      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("tank.tank");
        if (getTileEntity().inputTank.getFluid() != null) {
          heading += ": " + getTileEntity().inputTank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().inputTank));
      }
    });

    addToolTip(new GuiToolTip(new Rectangle(79, 9, 15, 47), "") {
      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("tank.tank");
        if (getTileEntity().outputTank.getFluid() != null) {
          heading += ": " + getTileEntity().outputTank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().outputTank));
      }
    });

    addToolTip(new GuiToolTip(new Rectangle(29, 77, 65, 3), "") {
      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("waterworks.stashprogress");
        heading += ": " + (int) (getTileEntity().stashProgress * 100) + "%";
        text.add(heading);
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

    if (getTileEntity().inputTank.getFluid() != null) {
      RenderUtil.renderGuiTank(getTileEntity().inputTank, guiLeft + 29, guiTop + 9, zLevel, 16, 47);
    }
    if (getTileEntity().outputTank.getFluid() != null) {
      RenderUtil.renderGuiTank(getTileEntity().outputTank, guiLeft + 78, guiTop + 9, zLevel, 16, 47);
    }

    if (getTileEntity().progress_in != null) {
      RenderUtil.bindBlockTexture();
      GL11.glEnable(GL11.GL_BLEND);
      float progress0 = getTileEntity().getProgress();
      if (progress0 >= 0) {
        float progress = MathHelper.clamp_float(progress0 * 1.3f - 0.15f, 0, 1);
        int cfactor = getTileEntity().progress_out != null ? Config.waterWorksWaterReductionPercentage : 0;
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
    }

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(GUI_TEXTURE);

    if (getTileEntity().stashProgress > 0.0) {
      int size = (int) (62 * getTileEntity().stashProgress) + 1;
      drawTexturedModalRect(guiLeft + 30, guiTop + 78, 188, 94, size, 1);
    }

    drawTexturedModalRect(guiLeft + 50, guiTop + 32, 208, 6, 24, 26); // boiler
    drawTexturedModalRect(guiLeft + 29, guiTop + 9, 196, 41, 16, 47); // left tank
    drawTexturedModalRect(guiLeft + 78, guiTop + 9, 214, 41, 16, 47); // right tank
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.bindBlockTexture();
  }

}
