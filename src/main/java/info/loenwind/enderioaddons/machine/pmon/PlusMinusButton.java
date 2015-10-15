package info.loenwind.enderioaddons.machine.pmon;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import com.enderio.core.client.gui.widget.GuiToolTip;

public class PlusMinusButton extends GuiButton {

  public static final int DEFAULT_WIDTH = 8;
  public static final int DEFAULT_HEIGHT = 6;

  private int xOrigin;
  private int yOrigin;

  protected GuiPMon gui;
  protected String[] toolTipText;
  protected boolean isPlus;

  private GuiToolTip toolTip;

  public PlusMinusButton(GuiPMon gui, int id, int x, int y, boolean isPlus) {
    super(id, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "");
    this.gui = gui;
    this.xOrigin = x;
    this.yOrigin = y;
    this.isPlus = isPlus;
  }

  public void setToolTip(String... tooltipText) {
    if (toolTip == null) {
      toolTip = new GuiToolTip(getBounds(), tooltipText);
    } else {
      toolTip.setToolTipText(tooltipText);
    }
    this.toolTipText = tooltipText;
  }

  protected void setToolTip(GuiToolTip newToolTip) {
    boolean addTooltip = false;
    if (toolTip != null) {
      addTooltip = gui.removeToolTip(toolTip);
    }
    toolTip = newToolTip;
    if (addTooltip && toolTip != null) {
      gui.addToolTip(toolTip);
    }
  }

  public final Rectangle getBounds() {
    return new Rectangle(xOrigin, yOrigin, getWidth(), getHeight());
  }

  public void onGuiInit() {
    gui.addButton(this);
    if (toolTip != null) {
      gui.addToolTip(toolTip);
    }
    xPosition = xOrigin + gui.getGuiLeft();
    yPosition = yOrigin + gui.getGuiTop();
  }

  public void detach() {
    gui.removeToolTip(toolTip);
    gui.removeButton(this);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public GuiToolTip getToolTip() {
    return toolTip;
  }

  @Override
  public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
    boolean result = super.mousePressed(par1Minecraft, par2, par3);
    if (result) {
      if (isPlus) {
        if (gui.timebase >= 6) {
          return false;
        }
        gui.timebase++;
        gui.timebaseOffset -= 16;
      } else {
        if (gui.timebase <= 0) {
          return false;
        }
        gui.timebase--;
        gui.timebaseOffset += 16;
      }
    }
    return result;
  }

  /**
   * Draws this button to the screen.
   */
  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (toolTip != null) {
      toolTip.setVisible(visible);
    }
  }

}
