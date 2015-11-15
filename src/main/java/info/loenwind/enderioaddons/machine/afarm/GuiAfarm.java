package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.gui.InvisibleButton;
import info.loenwind.enderioaddons.network.Manager;

import java.awt.Color;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.common.Optional;
import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiAfarm extends GuiPoweredMachineBase<TileAfarm> {

  private final static int tabXOffset = -3;
  private final static int tabYOffset = 4;
  private static final int TAB_WIDTH = 4 + 16 + 4;
  private static final int TAB_HEIGHT = 24;

  protected InvisibleButton[] tabButtons = new InvisibleButton[4];
  protected int tab = 0;
  private String texture = null;
  private CheckBox enabledB;
  private boolean tillBisAttached = false;

  public GuiAfarm(InventoryPlayer par1InventoryPlayer, @Nonnull TileAfarm te) {
    super(te, new ContainerAfarm(par1InventoryPlayer, te));

    for (int i = 0; i < tabButtons.length; i++) {
      tabButtons[i] = new InvisibleButton(this, -1, 0, 0);
    }

    enabledB = new CheckBox(this, -1, 34 + 1, 62 + 1);
    enabledB.setSelectedToolTip(EnderIOAddons.lang.localize("farm.gui.till.enabled").split("\\|"));
    enabledB.setUnselectedToolTip(EnderIOAddons.lang.localize("farm.gui.till.disabled").split("\\|"));
    enabledB.setSelected(te.tillAggresively);
  }

  @Override
  protected int getPowerHeight() {
    return 47;
  }

  @Override
  protected int getPowerY() {
    return 9;
  }

  private void updateVisibility() {
    ((ContainerAfarm) inventorySlots).setTabVisibility(tab, getTileEntity().twoGhosts());
    for (int i = 0; i < tabButtons.length; i++) {
      tabButtons[i].enabled = tab != i;
    }
    if (tab == 3 && !tillBisAttached) {
      enabledB.setSelected(getTileEntity().tillAggresively);
      enabledB.onGuiInit();
      tillBisAttached = true;
    } else if (tab != 3 && tillBisAttached) {
      enabledB.detach();
      tillBisAttached = false;
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    for (int i = 0; i < tabButtons.length; i++) {
      tabButtons[i].onGuiInit();
    }
    ((ContainerAfarm) inventorySlots).addGhostSlots(getGhostSlots());
    updateVisibility();
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    for (int i = 0; i < tabButtons.length; i++) {
      if (btn == tabButtons[i]) {
        tab = i;
        updateVisibility();
        return;
      }
    }
    if (btn == enabledB) {
      Manager.sendUpdateToServer(getTileEntity(), enabledB.isSelected() ? 1 : 0);
    }
    super.actionPerformed(btn);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    if (tab == 1 && getTileEntity().twoGhosts()) {
      texture = EnderIOAddons.DOMAIN + ":textures/gui/afarm1a.png";
    } else {
      texture = EnderIOAddons.DOMAIN + ":textures/gui/afarm" + tab + ".png";
    }
    RenderUtil.bindTexture(texture);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    drawTabs(sx, sy);
    drawTexts(sx, sy);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  protected void drawTexts(int sx, int sy) {
    int rgb = ColorUtil.getRGB(Color.white);
    switch (tab) {
    case 0:
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab0.modules"), sx + 34, sy + 7, rgb);
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab0.modulestorage"), sx + 34, sy + 35, rgb);
      break;
    case 1:
      if (getTileEntity().twoGhosts()) {
        fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.seed1"), sx + 34, sy + 7, rgb);
        fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.seed2"), sx + 88, sy + 7, rgb);
      } else {
        fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.nw"), sx + 34, sy + 7, rgb);
        fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.ne"), sx + 61, sy + 7, rgb);
        fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.se"), sx + 88, sy + 7, rgb);
        fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.sw"), sx + 115, sy + 7, rgb);
      }
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab1.seedstorage"), sx + 34, sy + 35, rgb);
      break;
    case 2:
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab2.output"), sx + 34, sy + 7, rgb);
      break;
    case 3:
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab3.cropsticks"), sx + 34, sy + 7, rgb);
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab3.tools"), sx + 34, sy + 35, rgb);
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab3.fertilizer"), sx + 88, sy + 35, rgb);
      fontRendererObj.drawStringWithShadow(EnderIOAddons.lang.localize("afarm.gui.tab3.till"), sx + 34 + 16 + 3, sy + 62 + 4, rgb);
      break;
    }
    RenderUtil.bindTexture(texture);
  }

  private int blockX0 = 0, blockX1 = 0, blockY0 = 0, blockY1 = 0;

  private void drawTabs(int sx, int sy) {
    int tabX = sx + xSize + tabXOffset;
    int tabY = sy + tabYOffset;
    // (1) Tabs
    for (int i = 0; i < tabButtons.length; i++) {
      IconEIO.map.render(i == tab ? IconEIO.ACTIVE_TAB : IconEIO.INACTIVE_TAB, tabX, tabY + i * TAB_HEIGHT, true);
      IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX + 5, tabY + i * TAB_HEIGHT, true);
      tabButtons[i].xPosition = tabX + 4;
      tabButtons[i].yPosition = tabY + i * TAB_HEIGHT + 4;
      tabButtons[i].width = 16;
      tabButtons[i].height = 16;
    }
    // (2) icons
    this.zLevel = 200.0F;
    itemRender.zLevel = 200.0F;
    itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(EnderIO.itemBasicFilterUpgrade), tabX + 4, tabY + 4);
    itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(Items.wheat_seeds), tabX + 4, tabY + TAB_HEIGHT + 4);
    itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(Items.wheat), tabX + 4, tabY + 2 * TAB_HEIGHT + 4);
    itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(Items.diamond_hoe), tabX + 4, tabY + 3 * TAB_HEIGHT + 4);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    this.zLevel = 0.0F;
    itemRender.zLevel = 0.0F;
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    // (4) block area for NEI
    blockX0 = tabX;
    blockX1 = tabX + TAB_WIDTH + 1;
    blockY0 = tabY;
    blockY1 = tabY + TAB_HEIGHT * 4;
    // (5) cleanup
    RenderUtil.bindTexture(texture);
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
    return (x + w) >= blockX0 && x <= blockX1 && (y + h) >= blockY0 && y <= blockY1;
  }

}
