package info.loenwind.enderioaddons.machine.tcom;

import info.loenwind.enderioaddons.machine.framework.TESRFrameworkMachine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

public class TESRTcom extends TESRFrameworkMachine {

  private final RendererTcom rendererTcom;

  public TESRTcom(RendererTcom rendererTcom) {
    super();
    this.rendererTcom = rendererTcom;
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    super.renderTileEntityAt(te, x, y, z, partialTick);
    if (te instanceof TileTcom) {
      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

      RenderUtil.bindBlockTexture();

      Tessellator.instance.startDrawingQuads();
      Tessellator.instance.addTranslation((float) x, (float) y, (float) z);
      rendererTcom.renderTileEntityAt((TileTcom) te);
      Tessellator.instance.addTranslation((float) -x, (float) -y, (float) -z);
      Tessellator.instance.draw();

      GL11.glPopAttrib();

    }
  }

}
