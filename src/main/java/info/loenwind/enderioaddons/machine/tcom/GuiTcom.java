package info.loenwind.enderioaddons.machine.tcom;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.gui.InvisibleButton;
import info.loenwind.enderioaddons.gui.StdOutputSlot;
import info.loenwind.enderioaddons.gui.StdSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiTcom extends GuiPoweredMachineBase<TileTcom> {

  private final static int SCROLL_STEP = 19;
  private final static int SCROLL_ITEMS = 3;

  protected int scrollPosition = 0;
  protected int scrollPositionOffset = 0;
  protected InvisibleButton plus;
  protected InvisibleButton minus;
  protected int maxScrollItems = 1;
  protected boolean isEnchantScreen = false;

  public GuiTcom(InventoryPlayer par1InventoryPlayer, @Nonnull TileTcom te) {
    super(te, new ContainerTcom(par1InventoryPlayer, te));

    plus = new InvisibleButton(this, 1, 59, 8);
    plus.setToolTip("+");
    minus = new InvisibleButton(this, 2, 59, 70);
    minus.setToolTip("-");
  }

  private void updateSlots() {
    ((StdSlot) ((ContainerTcom) inventorySlots).in0).enable(!isEnchantScreen);
    ((StdSlot) ((ContainerTcom) inventorySlots).in1).enable(isEnchantScreen);
    ((StdOutputSlot) ((ContainerTcom) inventorySlots).out0).enable(!isEnchantScreen);
    ((StdOutputSlot) ((ContainerTcom) inventorySlots).out1).enable(isEnchantScreen);
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.visible = false;
    plus.onGuiInit();
    minus.onGuiInit();
    updateSlots();
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    if (btn.id == 1) {
      if (scrollPosition >= maxScrollItems - SCROLL_ITEMS) {
        return;
      }
      scrollPosition++;
      scrollPositionOffset -= SCROLL_STEP;
    } else if (btn.id == 2) {
      if (scrollPosition <= 0) {
        return;
      }
      scrollPosition--;
      scrollPositionOffset += SCROLL_STEP;
    } else if (btn.id == 3) {
      isEnchantScreen = !isEnchantScreen;
      updateSlots();
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
    if (scrollPosition > maxScrollItems - SCROLL_ITEMS) {
      scrollPosition = maxScrollItems - SCROLL_ITEMS;
      scrollPositionOffset = 0;
    }
    if (scrollPosition < 0) {
      scrollPosition = 0;
      scrollPositionOffset = 0;
    }
  }

  private void updateScrollPositionPost() {
    if (lastTick != EnderIO.proxy.getTickCount()) {
      lastTick = EnderIO.proxy.getTickCount();
      if (scrollPositionOffset < 0) {
        scrollPositionOffset += 1 - scrollPositionOffset / (SCROLL_STEP / 2);
      } else if (scrollPositionOffset > 0) {
        scrollPositionOffset -= 1 + scrollPositionOffset / (SCROLL_STEP / 2);
      }
    }
  }

  // 55/15-34-69
  private void drawItems(int x, int y) {
    final Map<ItemStack, Float> materials = getTileEntity().engine.getMaterials();
    List<ItemStack> keyList = new ArrayList<>(materials.keySet());
    Collections.sort(keyList, new Comparator<ItemStack>() {
      @Override
      public int compare(ItemStack paramT1, ItemStack paramT2) {
        return Float.compare(materials.get(paramT1), materials.get(paramT2));
      }
    });
    if (maxScrollItems != materials.size()) {
      maxScrollItems = materials.size();
      updateScrollPositionPre();
    }
    int from = 0, to = SCROLL_ITEMS;
    if (scrollPositionOffset < 0) {
      from--;
    } else if (scrollPositionOffset > 0) {
      to++;
    }
    for (int i = from; i < to; i++) {
      int y0 = y + 15 + i * SCROLL_STEP - scrollPositionOffset;
      assert (scrollPosition + i) >= 0 : "scrollPosition" + scrollPosition + " scrollPositionOffset" + scrollPositionOffset + " i" + i + " from" + from + " to"
          + to;
      if (keyList.size() > (scrollPosition + i) && y0 >= 0) {
        final ItemStack itemStack = keyList.get(scrollPosition + i);
        final Float amount = materials.get(itemStack);
        drawMaterialLine(itemStack, amount.intValue(), x, y0);
        if (scrollPositionOffset > -2 && scrollPositionOffset < 2) {
          int barAmount = (int) ((amount - amount.intValue()) * 64f);
          drawTexturedModalRect(x + 23, y0 + 15, 191, 1, barAmount, 2);
        }
      }
    }

  }

  public void drawMaterialLine(ItemStack itemStack, int amount, int x, int y0) {
    drawFakeItemsStart();
    itemRender.zLevel = 0.0F;
    zLevel = 0.0F;
    drawFakeItemStack(x, y0, itemStack);

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_BLEND);
    if (amount > 1) {
      String s1 = String.valueOf(amount);
      fontRendererObj.drawStringWithShadow(s1, x + 19 - 2 - fontRendererObj.getStringWidth(s1), y0 + 6 + 3, 16777215);
    }

    String displayName = itemStack.getDisplayName();
    while (fontRendererObj.getStringWidth(displayName) > 43) {
      displayName = displayName.substring(0, displayName.length() - 1);
    }
    fontRendererObj.drawStringWithShadow(displayName, x + 22, y0 + 1, 16777215);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    drawFakeItemsEnd();
    RenderUtil.bindTexture(EnderIOAddons.DOMAIN + texture);
  }

  private String texture = null;

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    texture = isEnchantScreen ? ":textures/gui/tcomEnch.png" : ":textures/gui/tcomMats.png";
    RenderUtil.bindTexture(EnderIOAddons.DOMAIN + texture);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    updateScrollPositionPre();
    drawItems(sx + 55, sy);

    if (scrollPositionOffset != 0) {
      drawTexturedModalRect(sx + 54, sy, 0, 166, 88, 83);
    }
    updateScrollPositionPost();

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

  }

}
