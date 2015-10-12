package info.loenwind.enderioaddons.machine.voidtank;

import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

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
    if(item.stackTagCompound != null) {
      TileVoidTank tt = new TileVoidTank();
      tt.readCommon(item.stackTagCompound);
      FluidRendererVoidTank.renderTankFluid(tt, 0f, -0.1f, 0f);
    }

    GL11.glEnable(GL11.GL_ALPHA_TEST);
    Block block = BlockVoidTank.blockVoidTank;
    int meta = item.getItemDamage();
    
    // TODO need custom rendering, I think. or render unbroken tank?
    IIcon[] icons = RenderUtil.getBlockTextures(block, meta);
    BoundingBox bb = BoundingBox.UNIT_CUBE.translate(0, -0.1f, 0);
    Tessellator.instance.startDrawingQuads();
    FaceRenderer.renderCube(bb, icons, null, RenderUtil.getDefaultPerSideBrightness(), false);
    Tessellator.instance.draw();

    FluidRendererVoidTank.renderBlockContents(null, 0f, -0.1f, 0f);
  }

}
