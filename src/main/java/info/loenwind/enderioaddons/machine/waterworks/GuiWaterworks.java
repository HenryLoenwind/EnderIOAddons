package info.loenwind.enderioaddons.machine.waterworks;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiWaterworks extends GuiPoweredMachineBase<TileWaterworks> {

  public GuiWaterworks(InventoryPlayer par1InventoryPlayer, TileWaterworks te) {
    super(te, new ContainerWaterworks(par1InventoryPlayer, te));
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }


  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderioaddons:textures/gui/waterworks.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    drawTexturedModalRect(sx + 50, sy + 32, 208, 6, 24, 26); // boiler
    drawTexturedModalRect(sx + 30, sy + 9, 197, 41, 15, 47); // left tank
    drawTexturedModalRect(sx + 79, sy + 9, 215, 41, 15, 47); // right tank

    RenderUtil.bindBlockTexture();
  }

}
