package info.loenwind.enderioaddons.nei;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.machine.pmon.GuiPMon;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.enderio.core.client.render.ColorUtil;

public class TeaserRecipeHandler extends TemplateRecipeHandler {

  @Override
  public String getRecipeName() {
    return EnderIOAddons.lang.localize("nei.teaser");
  }

  @Override
  public String getGuiTexture() {
    return EnderIOAddons.DOMAIN + ":textures/gui/charger.png";
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiPMon.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EnderIOAddonsTeaser";
  }

  private TeaserRecipe tryToMakeARecipe(ItemStack itemstack) {
    if (itemstack != null && itemstack.getItem() != null) {
      final String unlocalizedName = itemstack.getItem().getUnlocalizedName(itemstack);
      if (unlocalizedName != null && unlocalizedName.startsWith("enderioaddons.")) {
        String key = "nei.teaser." + unlocalizedName;
        final String text = EnderIOAddons.lang.localize(key);
        if (text != null && !text.startsWith("enderioaddons.")) {
          final ItemStack copy = itemstack.copy();
          copy.stackSize = 1;
          return new TeaserRecipe(copy, key);
        }
      }
    }
    return null;
  }

  private TeaserRecipe tryToMakeImageRecipe(ItemStack itemstack) {
    if (itemstack != null && itemstack.getItem() != null) {
      final String unlocalizedName = itemstack.getItem().getUnlocalizedName(itemstack);
      if (unlocalizedName != null && unlocalizedName.startsWith("enderioaddons.")) {
        String key = "nei.teaser." + unlocalizedName + ".image";
        final String text = EnderIOAddons.lang.localize(key);
        if (text != null && !text.startsWith("enderioaddons.")) {
          final ItemStack copy = itemstack.copy();
          copy.stackSize = 1;
          final TeaserRecipe teaserRecipe = new TeaserRecipe(copy, null);
          teaserRecipe.texture = text;
          return teaserRecipe;
        }
      }
    }
    return null;
  }

  @Override
  public void loadCraftingRecipes(ItemStack itemstack) {
    TeaserRecipe maybe = tryToMakeARecipe(itemstack);
    if (maybe != null) {
      arecipes.add(maybe);
      maybe = tryToMakeImageRecipe(itemstack);
      if (maybe != null) {
        arecipes.add(maybe);
      }
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack itemstack) {
    TeaserRecipe maybe = tryToMakeARecipe(itemstack);
    if (maybe != null) {
      arecipes.add(maybe);
      maybe = tryToMakeImageRecipe(itemstack);
      if (maybe != null) {
        arecipes.add(maybe);
      }
    }
  }

  @Override
  public void drawBackground(int recipe) {
    if (((TeaserRecipe) arecipes.get(recipe)).texture != null) {
      String texture = EnderIOAddons.DOMAIN + ":textures/gui/" + ((TeaserRecipe) arecipes.get(recipe)).texture + ".png";
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GuiDraw.changeTexture(texture);
      drawTexturedModalRect(0, 0, 166, 166);
    }
  }

  private static void drawTexturedModalRect(int x, int y, int w, int h) {
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertexWithUV(x + 0, y + h, GuiDraw.gui.getZLevel(), 0, 1);
    tessellator.addVertexWithUV(x + w, y + h, GuiDraw.gui.getZLevel(), 1, 1);
    tessellator.addVertexWithUV(x + w, y + 0, GuiDraw.gui.getZLevel(), 1, 0);
    tessellator.addVertexWithUV(x + 0, y + 0, GuiDraw.gui.getZLevel(), 0, 0);
    tessellator.draw();
  }

  @Override
  public void drawExtras(int recipeIndex) {
    if (((TeaserRecipe) arecipes.get(recipeIndex)).textKey != null) {
      GuiDraw.fontRenderer.drawSplitString(EnderIOAddons.lang.localize(((TeaserRecipe) arecipes.get(recipeIndex)).textKey), 5, 16 + 18 + 6 - yOff, 160,
          ColorUtil.getRGB(Color.darkGray));
    }
  }

  @Override
  public int recipiesPerPage() {
    return 1;
  }

  protected static final int xOff = 13;
  protected static final int yOff = 11;

  public class TeaserRecipe extends TemplateRecipeHandler.CachedRecipe {

    private PositionedStack output;
    private String textKey;
    private String texture = null;

    @Override
    public List<PositionedStack> getIngredients() {
      return Collections.EMPTY_LIST;
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    public TeaserRecipe(ItemStack itemStack, String textKey) {
      output = new PositionedStack(itemStack, 85 - xOff, 16 - yOff);
      this.textKey = textKey;
    }
  }

}
