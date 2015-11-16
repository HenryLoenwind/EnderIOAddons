package info.loenwind.enderioaddons.machine.magcharger;

import static crazypants.enderio.EnderIO.proxy;
import info.loenwind.enderioaddons.common.Profiler;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TESRMagCharger extends TileEntitySpecialRenderer {

  private static final EntityItem item0 = new EntityItem(null, 0, 0, 0, new ItemStack(BlockFlag.blockFlag, 1, 0));
  private static final EntityItem item1 = new EntityItem(null, 0, 0, 0, new ItemStack(BlockFlag.blockFlag, 1, 1));
  static {
    item0.hoverStart = item1.hoverStart;
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    long id = Profiler.client.start();
    if (te instanceof TileMagCharger && ((TileMagCharger) te).isActive()) {
      TileMagCharger mc = ((TileMagCharger) te);
      Render renderItem = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
      final float progress = mc.getProgressScaled();
      if (progress < mc.renderLastProgress) {
        mc.renderOffset++;
      }
      mc.renderLastProgress = progress;
      int c = mc.itemsInQueue;
      if (c > 13) {
        c = 13;
      }
      EntityItem item = progress < .75 ? item1 : item0;
      for (int i = 0; i <= c; i++) {
        item.age = (int) proxy.getTickCount() - (mc.renderOffset + i) * 15;
        double yoffset = item.height * (i - progress) / 10d;
        double rotation = ((item.age + partialTick) / 20.0d + item.hoverStart) * (180d / Math.PI);
        double scale = (25d - i) / 28d;

        GL11.glPushMatrix();
        GL11.glTranslated(x + .5, y + .5 + yoffset, z + .5);
        GL11.glScaled(scale, scale, scale);
        GL11.glRotated(rotation, 0.0, 1.0, 0.0);
        GL11.glTranslated(5d / 16d, 0.0, 0.0);

        renderItem.doRender(item, 0, 0, 0, 0, partialTick);
        GL11.glPopMatrix();
        item = item1;
      }
    }
    Profiler.client.stop(id, "magcharger tesr");
  }


}
