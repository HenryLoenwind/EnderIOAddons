package info.loenwind.enderioaddons.machine.voidtank;

import info.loenwind.enderioaddons.EnderIOAddons;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiVoidTank extends GuiPoweredMachineBase<TileVoidTank> {

  public GuiVoidTank(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileVoidTank te) {
    super(te, new ContainerVoidTank(par1InventoryPlayer, te));

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
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    IIcon icon = Blocks.portal.getBlockTextureFromSide(1);
    RenderUtil.bindBlockTexture();
    GL11.glEnable(GL11.GL_BLEND);
    double minU = icon.getMinU();
    double maxU = icon.getMaxU();
    double minV = icon.getMinV();
    double maxV = icon.getMaxV();

    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    if (getTileEntity().isBroken()) {
      tessellator.addVertexWithUV(sx + 1, sy + ySize - 1, 0, minU, maxV);
      tessellator.addVertexWithUV(sx + xSize - 1, sy + ySize - 1, 0, maxU, maxV);
      tessellator.addVertexWithUV(sx + xSize - 1, sy + 1, 0, maxU, minV);
      tessellator.addVertexWithUV(sx + 1, sy + 1, 0, minU, minV);
    } else {
      tessellator.addVertexWithUV(sx + 113, sy + 29 + 32, 0, minU, maxV);
      tessellator.addVertexWithUV(sx + 113 + 32, sy + 29 + 32, 0, maxU, maxV);
      tessellator.addVertexWithUV(sx + 113 + 32, sy + 29, 0, maxU, minV);
      tessellator.addVertexWithUV(sx + 113, sy + 29, 0, minU, minV);
    }
    tessellator.draw();

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    if (getTileEntity().isBroken()) {
      RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/gui/voidtank.png");
    } else {
      RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/gui/voidtank_unbroken.png");
    }

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    GL11.glDisable(GL11.GL_BLEND);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.bindBlockTexture();
    RenderUtil.renderGuiTank(getTileEntity().tank, guiLeft + 80, guiTop + 36, zLevel, 16, 32);

  }

}
