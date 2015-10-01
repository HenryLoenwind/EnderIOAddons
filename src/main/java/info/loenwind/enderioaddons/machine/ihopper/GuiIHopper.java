package info.loenwind.enderioaddons.machine.ihopper;

import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.COL;
import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.D;
import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.ROW1;
import static info.loenwind.enderioaddons.machine.ihopper.ContainerIHopper.ROW2;
import static info.loenwind.enderioaddons.machine.ihopper.TileIHopper.SLOTS;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.gui.AdvancedRedstoneModeButton;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;

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
  protected void ghostSlotClicked(GhostSlot slot, int x, int y, int button) {
    ItemStack handStack = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
    ItemStack existingStack = slot.getStack();
    if (button == 0) { // left
      if (handStack == null || handStack.getItem() == null || handStack.stackSize == 0) { // empty hand
        slot.putStack(null);
      } else { // item in hand
        if (existingStack == null || existingStack.getItem() == null || existingStack.stackSize == 0) { // empty slot
          slot.putStack(handStack);
        } else { // filled slot
          if (ItemUtil.areStackMergable(existingStack, handStack)) { // same item
            if (existingStack.stackSize < existingStack.getMaxStackSize()) {
              existingStack.stackSize++;
              slot.putStack(existingStack);
            } else {
              // NOP
            }
          } else { // different item
            slot.putStack(handStack);
          }
        }
      }
    } else if (button == 1) { // right
      if (handStack == null || handStack.getItem() == null || handStack.stackSize == 0) { // empty hand
        slot.putStack(null);
      } else { // item in hand
        if (existingStack == null || existingStack.getItem() == null || existingStack.stackSize == 0) { // empty slot
          ItemStack oneItem = handStack.copy();
          oneItem.stackSize = 1;
          slot.putStack(oneItem);
        } else { // filled slot
          if (ItemUtil.areStackMergable(existingStack, handStack)) { // same item
            if (existingStack.stackSize > 1) {
              existingStack.stackSize--;
              slot.putStack(existingStack);
            } else {
              slot.putStack(null);
            }
          } else { // different item
            ItemStack oneItem = handStack.copy();
            oneItem.stackSize = 1;
            slot.putStack(oneItem);
          }
        }
      }
    } else if (button == -2) { // wheel up
      if (existingStack != null && existingStack.getItem() != null && existingStack.stackSize > 0 && existingStack.stackSize < existingStack.getMaxStackSize()) {
        existingStack.stackSize++;
        slot.putStack(existingStack);
      }
    } else if (button == -1) { // wheel down
      if (existingStack != null && existingStack.getItem() != null) {
        if (existingStack.stackSize > 1) {
          existingStack.stackSize--;
          slot.putStack(existingStack);
        } else {
          slot.putStack(null);
        }
      }
    }
  }

  @Override
  protected void mouseWheel(int x, int y, int delta) {
    if (!ghostSlots.isEmpty()) {
      GhostSlot slot = getGhostSlot(x, y);
      if (slot != null) {
        ghostSlotClicked(slot, x, y, delta < 0 ? -1 : -2);
        return;
      }
    }
    super.mouseWheel(x, y, delta);
  }

  @Override
  protected void drawFakeItemStack(int x, int y, ItemStack stack) {
    itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y);
    itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y, null);
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

    RenderUtil.bindBlockTexture();
  }

  private boolean doneGraying = false;

  @Override
  protected void drawFakeItemHover(int x, int y) {
    doGraying();
    doneGraying = true;
    super.drawFakeItemHover(x, y);
  }

  private void doGraying() {
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
    RenderUtil.bindTexture(GUI_TEXTURE);
    GL11.glEnable(GL11.GL_BLEND);
    drawTexturedModalRect(guiLeft + 43, guiTop + 35, 43, 35, 108, 18);
    GL11.glDisable(GL11.GL_BLEND);
  }

  @Override
  protected void drawFakeItemsEnd() {
    if (!doneGraying) {
      doGraying();
    }
    doneGraying = false;
    super.drawFakeItemsEnd();
  }

}
