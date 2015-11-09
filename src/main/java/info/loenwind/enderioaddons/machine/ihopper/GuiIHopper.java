package info.loenwind.enderioaddons.machine.ihopper;

import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.COL;
import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.D;
import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.ROW1;
import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.ROW2;
import static info.loenwind.enderioaddons.machine.ihopper.TileIHopper.SLOTS;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.gui.AdvancedRedstoneModeButton;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiIHopper extends GuiPoweredMachineBase<TileIHopper> {

  @Nonnull
  private static final String GUI_TEXTURE = EnderIOAddons.DOMAIN + ":textures/gui/impulsehopper.png";

  protected AdvancedRedstoneModeButton advancedRedstoneButton;

  public GuiIHopper(InventoryPlayer par1InventoryPlayer, @Nonnull TileIHopper te) {
    super(te, new ContainerIHopper(par1InventoryPlayer, te));

    int x = getXSize() - 5 - BUTTON_SIZE;
    int y = 5;
    advancedRedstoneButton = new AdvancedRedstoneModeButton(this, -1, x, y, te, new BlockCoord(te));
    redstoneButton.visible = false;
  }

  @Override
  public void initGui() {
    super.initGui();
    ((ContainerIHopper) inventorySlots).addGhostSlots(getGhostSlots());
    advancedRedstoneButton.onGuiInit();
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

    TileIHopper te = getTileEntity();
    for (int slot = 1; slot <= SLOTS; slot++) {
      if (te.checkGhostSlot(slot)) {
        if (te.checkInputSlot(slot)) {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW1 + D - 1, 200, D / 2, D, D / 2);
        } else {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW1 + D - 1, 200, 0, D, D / 2);
        }
        if (te.checkOutputSlot(slot)) {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW2 + D - 1, 200, D / 2, D, D / 2);
        } else {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW2 + D - 1, 200, 0, D, D / 2);
        }
      }
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
