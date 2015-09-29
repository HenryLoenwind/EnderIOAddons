package info.loenwind.enderioaddons.machine.ihopper;

import info.loenwind.enderioaddons.EnderIOAddons;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiIHopper extends GuiPoweredMachineBase<TileIHopper> {

  @Nonnull
  private static final String GUI_TEXTURE = EnderIOAddons.DOMAIN + ":textures/gui/impulsehopper.png";

  public GuiIHopper(InventoryPlayer par1InventoryPlayer, @Nonnull TileIHopper te) {
    super(te, new ContainerIHopper(par1InventoryPlayer, te));

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

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.bindBlockTexture();
  }

}
