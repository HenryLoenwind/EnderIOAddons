package info.loenwind.enderioaddons.machine.chassis;

import info.loenwind.enderioaddons.render.FaceRenderer;
import info.loenwind.enderioaddons.render.ItemRendererBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

public class ItemRendererChassis extends ItemRendererBase {

  @Override
  protected void render(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    GL11.glDisable(GL11.GL_CULL_FACE);
    IIcon[] icons = RenderUtil.getBlockTextures(BlockChassis.blockChassis, item.getItemDamage());
    BoundingBox bb = BoundingBox.UNIT_CUBE;
    Tessellator.instance.startDrawingQuads();
    FaceRenderer.renderCube(bb, icons, null, FaceRenderer.stdBrightness, false);
    Tessellator.instance.draw();
  }

}
