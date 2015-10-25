package info.loenwind.enderioaddons.nei;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.flag.ItemFlag;
import info.loenwind.enderioaddons.machine.magcharger.GuiMagCharger;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.enderio.core.client.render.EnderWidget;

import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class MagChargerRecipeHandler extends TemplateRecipeHandler {

  @Override
  public String getRecipeName() {
    return EnderIOAddons.lang.localize("nei.magcharger");
  }

  @Override
  public String getGuiTexture() {
    return EnderIOAddons.DOMAIN + ":textures/gui/charger.png";
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiMagCharger.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EnderIOAddonsMagCharger";
  }

  @Override
  public void loadTransferRects() { // TODO ???
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(149, 32, 16, 16), "EnderIOAddonsMagCharger", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(ItemStack itemstack) {
    if (itemstack != null && itemstack.getItem() instanceof ItemFlag) {
      if (itemstack.getItemDamage() == 0) {
        arecipes.add(new MagChargerRecipe());
      }
    }
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results) {
    if (outputId.equals("EnderIOAddonsMagCharger") && getClass() == MagChargerRecipeHandler.class) {
      arecipes.add(new MagChargerRecipe());
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack itemstack) {
    if (itemstack != null && itemstack.getItem() instanceof ItemFlag) {
      if (itemstack.getItemDamage() != 0) {
        arecipes.add(new MagChargerRecipe());
      }
    }
  }

  @Override
  public void drawBackground(int recipeIndex) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GuiDraw.changeTexture(getGuiTexture());
    GuiDraw.drawTexturedModalRect(16, 0, 29, 11, 142, 65);
  }

  @Override
  public void drawExtras(int recipeIndex) {
    drawProgressBar(80 - xOff, 34 - yOff, 176, 14, 25, 16, Config.magcMagnetizingTicksPerItem.getInt(), 0);

    int energyUse = Config.magcMagnetizingTicksPerItem.getInt() * Config.magcMagnetizingPowerPerTick.getInt();
    String energyString = PowerDisplayUtil.formatPower(energyUse) + " " + PowerDisplayUtil.abrevation();
    GuiDraw.drawString(energyString, 100, 57, 0x808080, false);

    int x = 149, y = 32;
    EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
    IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
  }

  protected static final int xOff = 13;
  protected static final int yOff = 11;

  public class MagChargerRecipe extends TemplateRecipeHandler.CachedRecipe {


    private ArrayList<PositionedStack> input;
    private PositionedStack output;

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 20, input);
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    public MagChargerRecipe() {
      input = new ArrayList<PositionedStack>();

      input.add(new PositionedStack(new ItemStack(BlockFlag.blockFlag, 1, 1), 59 - xOff, 34 - yOff));

      output = new PositionedStack(new ItemStack(BlockFlag.blockFlag, 1, 0), 112 - xOff, 34 - yOff);
    }
  }

}
