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

  private TeaserRecipe tryToMakeARecipe(ItemStack itemstack, int pageid) {
    if (itemstack != null && itemstack.getItem() != null) {
      final String unlocalizedName = itemstack.getItem().getUnlocalizedName(itemstack);
      if (unlocalizedName != null && unlocalizedName.startsWith("enderioaddons.")) {
        String text = getText(unlocalizedName, pageid);
        String image = getImage(unlocalizedName, pageid);
        if (text != null || image != null) {
          final ItemStack copy = itemstack.copy();
          copy.stackSize = 1;
          final TeaserRecipe teaserRecipe = new TeaserRecipe(copy, text, image);
          return teaserRecipe;
        }
      }
    }
    return null;
  }

  private static String getText(String unlocalizedName, int pageid) {
    String key = "nei.teaser." + unlocalizedName + (pageid < 0 ? "" : ".page" + pageid);
    String text = EnderIOAddons.lang.localize(key);
    if (text != null && !text.startsWith("enderioaddons.")) {
      return key;
    }
    return null;
  }

  private static String getImage(String unlocalizedName, int pageid) {
    String key = "nei.teaser." + unlocalizedName + (pageid < 0 ? "" : ".page" + pageid) + ".image";
    String text = EnderIOAddons.lang.localize(key);
    if (text != null && !text.startsWith("enderioaddons.")) {
      return text;
    }
    return null;
  }

  @Override
  public void loadCraftingRecipes(ItemStack itemstack) {
    int pageid = 0;
    while (true) {
      TeaserRecipe maybe = tryToMakeARecipe(itemstack, pageid);
      if (maybe != null) {
        arecipes.add(maybe);
        pageid++;
      } else {
        if (pageid == 0) {
          maybe = tryToMakeARecipe(itemstack, -1);
          if (maybe != null) {
            arecipes.add(maybe);
          }
        }
        return;
      }
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack itemstack) {
    loadCraftingRecipes(itemstack);
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

    private final PositionedStack output;
    private final String textKey;
    private final String texture;

    @Override
    public List<PositionedStack> getIngredients() {
      return Collections.EMPTY_LIST;
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    public TeaserRecipe(ItemStack itemStack, String textKey, String image) {
      this.output = new PositionedStack(itemStack, 85 - xOff, 16 - yOff);
      this.textKey = textKey;
      this.texture = image;
    }
  }

}
