package info.loenwind.enderioaddons.machine.voidtank;

import static info.loenwind.enderioaddons.machine.voidtank.RendererVoidTank.makeBlockTextureList;
import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;

public class ItemRendererVoidTank implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    if(data != null && data.length > 0) {
      renderToInventory(item);
    }
  }

  public static void renderToInventory(ItemStack item) {
    boolean isBroken = false;
    if(item.stackTagCompound != null) {
      TileVoidTank tt = new TileVoidTank();
      tt.readCommon(item.stackTagCompound);
      isBroken = tt.isBroken();
      FluidRendererVoidTank.renderTankFluid(tt, 0f, -0.1f, 0f);
    }

    GL11.glEnable(GL11.GL_ALPHA_TEST);
    IIcon[] icons = isBroken ? RenderUtil.getBlockTextures(BlockVoidTank.blockVoidTank, 0) : makeBlockTextureList(EnderIO.blockTank.getIcon(3, 1));
    BoundingBox bb = BoundingBox.UNIT_CUBE.translate(0, -0.1f, 0);
    Tessellator.instance.startDrawingQuads();
    FaceRenderer.renderCube(bb, icons, null, RenderUtil.getDefaultPerSideBrightness(), false);
    Tessellator.instance.draw();

    if (isBroken) {
      FluidRendererVoidTank.renderBlockContents(null, 0f, -0.1f, 0f);
    } else {
      FluidRendererVoidTank.renderBlockContentsUnbroken(0f, -0.1f, 0f);
    }
  }

}
