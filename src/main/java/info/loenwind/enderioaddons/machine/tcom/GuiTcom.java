package info.loenwind.enderioaddons.machine.tcom;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.gui.InvisibleButton;
import info.loenwind.enderioaddons.gui.StdOutputSlot;
import info.loenwind.enderioaddons.gui.StdSlot;
import info.loenwind.enderioaddons.machine.tcom.engine.Mats;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.common.Optional;
import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;

public class GuiTcom extends GuiPoweredMachineBase<TileTcom> {

  private final static int SCROLL_STEP = 19;
  private final static int SCROLL_ITEMS = 3;
  private final static int tabXOffset = -3;
  private final static int tabYOffset = 4;
  private static final int TAB_WIDTH = 4 + 16 + 4;
  private static final int TAB_HEIGHT = 24;

  protected int scrollPosition = 0;
  protected int scrollPositionOffset = 0;
  protected InvisibleButton plus, minus, tab_mats, tab_enchant;
  protected int maxScrollItems = 1;
  protected boolean isEnchantScreen = false;
  protected final InvisibleButton[] getButtons = new InvisibleButton[SCROLL_ITEMS];
  protected final GuiToolTip[] enchTooltips = new GuiToolTip[SCROLL_ITEMS];

  private String texture = null;
  private long lastUpdateRequest = 0;

  public GuiTcom(InventoryPlayer par1InventoryPlayer, @Nonnull TileTcom te) {
    super(te, new ContainerTcom(par1InventoryPlayer, te));

    plus = new InvisibleButton(this, 1, 59, 8);
    minus = new InvisibleButton(this, 2, 59, 70);
    tab_mats = new InvisibleButton(this, 3, 0, 0);
    tab_enchant = new InvisibleButton(this, 4, 0, 0);

    addToolTip(enchTooltips[0] = new GuiToolTip(new Rectangle(54, 14, 88, 18), (List<String>) null));
    addToolTip(enchTooltips[1] = new GuiToolTip(new Rectangle(54, 33, 88, 18), (List<String>) null));
    addToolTip(enchTooltips[2] = new GuiToolTip(new Rectangle(54, 52, 88, 18), (List<String>) null));

    getButtons[0] = new InvisibleButton(this, 100, 124, 14); // TODO: tooltips
    getButtons[1] = new InvisibleButton(this, 101, 124, 33);
    getButtons[2] = new InvisibleButton(this, 102, 124, 52);
    getButtons[0].width = getButtons[1].width = getButtons[2].width = 18;
    getButtons[0].height = getButtons[1].height = getButtons[2].height = 12;

    lastUpdateRequest = EnderIO.proxy.getTickCount() + 10;
    PacketHandler.INSTANCE.sendToServer(new PacketTcomAction(getTileEntity()));
  }

  private void updateVisibility() {
    ((StdSlot) ((ContainerTcom) inventorySlots).in0).enable(!isEnchantScreen);
    ((StdSlot) ((ContainerTcom) inventorySlots).in1).enable(isEnchantScreen);
    ((StdOutputSlot) ((ContainerTcom) inventorySlots).out0).enable(!isEnchantScreen);
    ((StdOutputSlot) ((ContainerTcom) inventorySlots).out1).enable(isEnchantScreen);
    plus.enabled = scrollPosition < maxScrollItems - SCROLL_ITEMS;
    minus.enabled = scrollPosition > 0;
    tab_mats.enabled = isEnchantScreen;
    tab_enchant.enabled = !isEnchantScreen;
    enchTooltips[0].setVisible(isEnchantScreen && maxScrollItems >= 1 && scrollPositionOffset == 0);
    enchTooltips[1].setVisible(isEnchantScreen && maxScrollItems >= 2 && scrollPositionOffset == 0);
    enchTooltips[2].setVisible(isEnchantScreen && maxScrollItems >= 3 && scrollPositionOffset == 0);
    getButtons[0].enabled = maxScrollItems >= 1 && scrollPositionOffset == 0;
    getButtons[1].enabled = maxScrollItems >= 2 && scrollPositionOffset == 0;
    getButtons[2].enabled = maxScrollItems >= 3 && scrollPositionOffset == 0;
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.visible = false;
    plus.onGuiInit();
    minus.onGuiInit();
    tab_mats.onGuiInit();
    tab_enchant.onGuiInit();
    getButtons[0].onGuiInit();
    getButtons[1].onGuiInit();
    getButtons[2].onGuiInit();
    updateVisibility();
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    if (btn == plus) {
      if (scrollPosition >= maxScrollItems - SCROLL_ITEMS) {
        return;
      }
      scrollPosition++;
      scrollPositionOffset -= SCROLL_STEP;
    } else if (btn == minus) {
      if (scrollPosition <= 0) {
        return;
      }
      scrollPosition--;
      scrollPositionOffset += SCROLL_STEP;
    } else if (btn == tab_mats) {
      isEnchantScreen = false;
      scrollPosition = scrollPositionOffset = 0;
    } else if (btn == tab_enchant) {
      isEnchantScreen = true;
      scrollPosition = scrollPositionOffset = 0;
    } else if (btn.id >= 100 && btn.id <= 102) {
      if (isEnchantScreen) {
        Map<Enchantment, Float> enchantments = getCurrentEnchantmentList();
        List<Enchantment> keyList = sortEnchantmentList(enchantments);
        Enchantment enchantment = keyList.get(btn.id - 100 + scrollPosition);
        if (enchantment != null) {
          PacketHandler.INSTANCE.sendToServer(new PacketTcomAction(getTileEntity(), enchantment.effectId));
        }
      } else {
        final Map<ItemStack, Float> materials = getTileEntity().engine.getMaterials();
        List<ItemStack> keyList = sortMaterialsList(materials);
        Mats mat = Mats.getMat(keyList.get(btn.id - 100 + scrollPosition));
        if (mat != null) {
          PacketHandler.INSTANCE.sendToServer(new PacketTcomAction(getTileEntity(), mat));
        }
      }
    } else {
      super.actionPerformed(btn);
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected int getPowerX() {
    return 8;
  }

  @Override
  protected int getPowerY() {
    return 10;
  }

  @Override
  protected int getPowerWidth() {
    return 4;
  }

  @Override
  protected int getPowerHeight() {
    return 66;
  }

  private long lastTick = 0;

  private void updateScrollPositionPre() {
    if (isEnchantScreen) {
      maxScrollItems = getCurrentEnchantmentList().size();
    } else {
      maxScrollItems = getTileEntity().engine.getMaterials().size();
    }
    if (scrollPosition > maxScrollItems - SCROLL_ITEMS) {
      scrollPosition = maxScrollItems - SCROLL_ITEMS;
      scrollPositionOffset = 0;
    }
    if (scrollPosition < 0) {
      scrollPosition = 0;
      scrollPositionOffset = 0;
    }
    updateVisibility();
  }

  private Map<Enchantment, Float> getCurrentEnchantmentList() {
    return getTileEntity().engine.getEnchantments(getTileEntity().getStackInSlot((((ContainerTcom) inventorySlots).in1).slotNumber));
  }

  private void updateScrollPositionPost() {
    if (lastTick < EnderIO.proxy.getTickCount()) {
      lastTick = EnderIO.proxy.getTickCount();
      if (scrollPositionOffset < 0) {
        scrollPositionOffset += 1 - scrollPositionOffset / (SCROLL_STEP / 2);
      } else if (scrollPositionOffset > 0) {
        scrollPositionOffset -= 1 + scrollPositionOffset / (SCROLL_STEP / 2);
      }
    }
  }

  private void drawEnchants(int x, int y) {
    final Map<Enchantment, Float> enchantments = getCurrentEnchantmentList();
    List<Enchantment> keyList = sortEnchantmentList(enchantments);
    int line = 0;
    for (int i = 0; i < keyList.size(); i++) {
      int y0 = y + 15 + (i - scrollPosition) * SCROLL_STEP - scrollPositionOffset;
      if (y0 >= y && y0 < y + 69) {
        final Enchantment enchantment = keyList.get(i);
        final Float amount = enchantments.get(enchantment);
        drawEnchantmentText(enchantment, x, y0);
        if (scrollPositionOffset > -2 && scrollPositionOffset < 2) {
          int x10 = amount.intValue() / 10;
          float m10 = amount - x10;
          if (x10 > 10) {
            x10 = 10;
            m10 = 10;
          }
          int barAmount1 = (int) (x10 * 86f / 10f);
          int barAmount2 = (int) (m10 * 86f / 10f);
          drawProgressLine(x + 1, y0 + 14, barAmount1, 1);
          drawProgressLine(x + 1, y0 + 15, barAmount2, 1);
        }
        if (scrollPositionOffset == 0 && line < enchTooltips.length) {
          enchTooltips[line++].setToolTipText(amount + " levels"); // TODO localize
        }
      }
    }
  }

  private static List<Enchantment> sortEnchantmentList(final Map<Enchantment, Float> enchantments) {
    List<Enchantment> keyList = new ArrayList<>(enchantments.keySet());
    Collections.sort(keyList, new Comparator<Enchantment>() {
      @Override
      public int compare(Enchantment paramT1, Enchantment paramT2) {
        return Float.compare(enchantments.get(paramT2), enchantments.get(paramT1));
      }
    });
    return keyList;
  }

  private void drawEnchantmentText(Enchantment enchantment, int x, int y0) {
    String displayName = StatCollector.translateToLocal(enchantment.getName());
    while (fontRendererObj.getStringWidth(displayName) > 68) {
      displayName = displayName.substring(0, displayName.length() - 1);
    }
    fontRendererObj.drawStringWithShadow(displayName, x + 1, y0 + 1, 16777215);
  }

  private void drawItems(int x, int y) {
    final Map<ItemStack, Float> materials = getTileEntity().engine.getMaterials();
    List<ItemStack> keyList = sortMaterialsList(materials);
    for (int i = 0; i < keyList.size(); i++) {
      int y0 = y + 15 + (i - scrollPosition) * SCROLL_STEP - scrollPositionOffset;
      if (y0 >= y && y0 < y + 69) {
        final ItemStack itemStack = keyList.get(i);
        final Float amount = materials.get(itemStack);
        drawMaterialLine(itemStack, x, y0);
        if (scrollPositionOffset > -2 && scrollPositionOffset < 2) {
          int barAmount = (int) ((amount - amount.intValue()) * 64f);
          drawProgressLine(x + 23, y0 + 14, barAmount, 2);
        }
      }
    }
    if (scrollPositionOffset != 0) {
      RenderUtil.bindTexture(texture);
      drawScrollMask1(x, y);
    }
    for (int i = 0; i < keyList.size(); i++) {
      int y0 = y + 15 + (i - scrollPosition) * SCROLL_STEP - scrollPositionOffset;
      if (y0 >= y && y0 < y + 69) {
        final ItemStack itemStack = keyList.get(i);
        final Float amount = materials.get(itemStack);
        drawMaterialLineAmount(amount.intValue(), x, y0);
      }
    }
  }

  public static List<ItemStack> sortMaterialsList(final Map<ItemStack, Float> materials) {
    List<ItemStack> keyList = new ArrayList<>(materials.keySet());
    Collections.sort(keyList, new Comparator<ItemStack>() {
      @Override
      public int compare(ItemStack paramT1, ItemStack paramT2) {
        return Float.compare(materials.get(paramT2), materials.get(paramT1));
      }
    });
    return keyList;
  }

  private void drawProgressLine(int x, int y, int length, int lines) {
    RenderUtil.bindTexture(texture);
    drawTexturedModalRect(x, y, 1, 250, length, lines);
  }

  private void drawMaterialLine(ItemStack itemStack, int x, int y0) {
    drawFakeItemsStart();
    drawFakeItemStack(x + 1, y0, itemStack);

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_BLEND);

    String displayName = itemStack.getDisplayName();
    while (fontRendererObj.getStringWidth(displayName) > 46) {
      displayName = displayName.substring(0, displayName.length() - 1);
    }
    fontRendererObj.drawStringWithShadow(displayName, x + 23, y0 + 1, 16777215);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    drawFakeItemsEnd();
  }

  private void drawMaterialLineAmount(int amount, int x, int y0) {
    if (amount > 1) {
      drawFakeItemsStart();
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      String s1 = amount >= 1000 ? String.valueOf(amount / 1000) + "k" : String.valueOf(amount);
      fontRendererObj.drawStringWithShadow(s1, x + 20 - 2 - fontRendererObj.getStringWidth(s1), y0 + 6 + 3, 16777215);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      drawFakeItemsEnd();
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    texture = EnderIOAddons.DOMAIN + (isEnchantScreen ? ":textures/gui/tcomEnch.png" : ":textures/gui/tcomMats.png");
    RenderUtil.bindTexture(texture);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    drawTabs(sx, sy);

    updateScrollPositionPre();
    if (isEnchantScreen) {
      drawEnchants(sx + 54, sy);
    } else {
      drawItems(sx + 54, sy);
    }

    RenderUtil.bindTexture(texture);
    if (scrollPositionOffset != 0) {
      drawScrollMask2(sx + 54, sy);
    }
    updateScrollPositionPost();

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    if (EnderIO.proxy.getTickCount() > lastUpdateRequest) {
      lastUpdateRequest = EnderIO.proxy.getTickCount() + 10;
      PacketHandler.INSTANCE.sendToServer(new PacketTcomAction(getTileEntity()));
    }
  }

  private int blockX0 = 0, blockX1 = 0, blockY0 = 0, blockY1 = 0;

  private void drawTabs(int sx, int sy) {
    int tabX = sx + xSize + tabXOffset;
    int tabY = sy + tabYOffset;
    // (1) Tabs
    if (isEnchantScreen) {
      IconEIO.map.render(IconEIO.INACTIVE_TAB, tabX, tabY, true);
      IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX, tabY + TAB_HEIGHT, true);
    } else {
      IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX, tabY, true);
      IconEIO.map.render(IconEIO.INACTIVE_TAB, tabX, tabY + TAB_HEIGHT, true);
    }
    IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX + 5, tabY, true);
    IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX + 5, tabY + TAB_HEIGHT, true);
    // (2) icons
    itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(Blocks.planks), tabX + 4, tabY + 4);
    itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(Items.enchanted_book), tabX + 4, tabY + TAB_HEIGHT + 4);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    // (3) buttons
    tab_mats.xPosition = tabX + 4;
    tab_mats.yPosition = tabY + 4;
    tab_mats.width = 16;
    tab_mats.height = 16;
    tab_enchant.xPosition = tabX + 4;
    tab_enchant.yPosition = tabY + TAB_HEIGHT + 4;
    tab_enchant.width = 16;
    tab_enchant.height = 16;
    // (4) block area for NEI
    blockX0 = tabX;
    blockX1 = tabX + TAB_WIDTH + 1;
    blockY0 = tabY;
    blockY1 = tabY + TAB_HEIGHT * 2;
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
    return (x + w) >= blockX0 && x <= blockX1 && (y + h) >= blockY0 && y <= blockY1;
  }

  private void drawScrollMask1(int sx, int sy) {
    RenderUtil.bindTexture(texture);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    drawTexturedModalRect(sx, sy, 88, 166, 88, 83);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
  }

  private void drawScrollMask2(int sx, int sy) {
    RenderUtil.bindTexture(texture);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    drawTexturedModalRect(sx, sy, 0, 166, 88, 83);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
  }

}
