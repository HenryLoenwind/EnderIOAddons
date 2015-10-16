package info.loenwind.enderioaddons.machine.pmon;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TESRPMon extends TileEntitySpecialRenderer {

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    renderBlockContents((TilePMon) te, (float) x, (float) y, (float) z);
  }

  public static void renderBlockContents(TilePMon te, float x, float y, float z) {
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.addTranslation(x, y, z);
    RendererPMon.renderTileEntityAt(te);
    Tessellator.instance.addTranslation(-x, -y, -z);
    Tessellator.instance.draw();

    GL11.glPopAttrib();
  }

}
