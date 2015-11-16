package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.Profiler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

public class TESRAfarm extends TileEntitySpecialRenderer {

  @Override
  public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
    long id = Profiler.client.start();
    if (tile instanceof TileAfarm) {
      TileAfarm te = (TileAfarm) tile;
      if (!te.notifications.isEmpty()) {
        double offset = 0;
        GL11.glDisable(GL11.GL_LIGHTING);
        for (Notif notif : te.notifications) {
          if (notif.show()) {
            String toRender = EnderIOAddons.lang.localize("afarm.notification." + notif.name());
            RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.2 + offset, z + 0.5), toRender, 0.25f);
            offset += 0.375;
          }
        }
        GL11.glEnable(GL11.GL_LIGHTING);
      }
    }
    Profiler.client.stop(id, "farm tesr");
  }

}
