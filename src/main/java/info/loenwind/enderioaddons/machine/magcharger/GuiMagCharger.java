package info.loenwind.enderioaddons.machine.magcharger;

import info.loenwind.enderioaddons.EnderIOAddons;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiMagCharger extends GuiPoweredMachineBase<TileMagCharger> {

  public GuiMagCharger(InventoryPlayer par1InventoryPlayer, @Nonnull TileMagCharger te) {
    super(te, new ContainerMagCharger(par1InventoryPlayer, te));
  }

  @Override
  protected boolean showRecipeButton() {
    return true;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/gui/charger.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if (getTileEntity().isActive()) {
      int progress = (int) (getTileEntity().getProgressScaled() * 24);
      drawTexturedModalRect(sx + 80, sy + 34, 176, 14, progress + 1, 16);
      updateProgressTooltips(scaleProgressForTooltip(getTileEntity().getProgressScaled()), getTileEntity().getProgressScaled());
    } else {
      updateProgressTooltips(-1, -1);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

  }

}
