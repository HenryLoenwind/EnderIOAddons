package info.loenwind.enderioaddons.machine.pmon;

import info.loenwind.enderioaddons.render.FaceRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

public class ItemRendererPMon implements IItemRenderer {

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
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    IIcon[] icons = RenderUtil.getBlockTextures(BlockPMon.blockPMon, 0);
    BoundingBox bb = BoundingBox.UNIT_CUBE.translate(0, -0.1f, 0);
    Tessellator.instance.startDrawingQuads();
    FaceRenderer.renderCube(bb, icons, null, RenderUtil.getDefaultPerSideBrightness(), false);
    Tessellator.instance.draw();
  }

}
