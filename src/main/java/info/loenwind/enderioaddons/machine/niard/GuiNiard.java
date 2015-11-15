package info.loenwind.enderioaddons.machine.niard;

import info.loenwind.enderioaddons.EnderIOAddons;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiNiard extends GuiPoweredMachineBase<TileNiard> {

  public GuiNiard(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileNiard te) {
    super(te, new ContainerNiard(par1InventoryPlayer, te));

    addToolTip(new GuiToolTip(new Rectangle(80, 21, 16, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("tank.tank");
        if (getTileEntity().tank.getFluid() != null) {
          heading += ": " + getTileEntity().tank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().tank));
      }

    });
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/gui/niard.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    RenderUtil.bindBlockTexture();
    RenderUtil.renderGuiTank(getTileEntity().tank, guiLeft + 80, guiTop + 21, zLevel, 16, 47);

    renderFlowingFluid(getTileEntity().tank.getFluid(), sx + 112, sy + 28, 39, 56);

    RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/gui/niard.png");
    GL11.glEnable(GL11.GL_BLEND);
    drawTexturedModalRect(sx + 112, sy + 28, 200, 0, 39, 56);
    GL11.glDisable(GL11.GL_BLEND);
  }

  public static void renderFlowingFluid(FluidStack fluid, double x, double y, double width, double height) {
    if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
      return;
    }

    IIcon icon = fluid.getFluid().getFlowingIcon();
    if (icon == null) {
      icon = fluid.getFluid().getIcon();
      if (icon == null) {
        return;
      }
    }

    RenderUtil.bindBlockTexture();
    int color = fluid.getFluid().getColor(fluid);
    GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

    GL11.glEnable(GL11.GL_BLEND);
    for (int i = 0; i < width; i += 16) {
      for (int j = 0; j < (int) height; j += 16) {
        int drawWidth = (int) Math.min(width - i, 16);
        int drawHeight = Math.min((int) height - j, 16);

        int drawX = (int) (x + i);
        int drawY = (int) y + j;

        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(drawX, drawY + drawHeight, 0, minU, minV + (maxV - minV) * drawHeight / 16F);
        tessellator.addVertexWithUV(drawX + drawWidth, drawY + drawHeight, 0, minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F);
        tessellator.addVertexWithUV(drawX + drawWidth, drawY, 0, minU + (maxU - minU) * drawWidth / 16F, minV);
        tessellator.addVertexWithUV(drawX, drawY, 0, minU, minV);
        tessellator.draw();
      }
    }
    GL11.glDisable(GL11.GL_BLEND);
  }

}
