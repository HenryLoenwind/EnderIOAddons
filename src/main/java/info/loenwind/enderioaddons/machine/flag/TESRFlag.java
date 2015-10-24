package info.loenwind.enderioaddons.machine.flag;

import static crazypants.enderio.EnderIO.proxy;
import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import static info.loenwind.enderioaddons.render.FaceRenderer.setupVertices;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.client.render.VertexTransformComposite;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TESRFlag extends TileEntitySpecialRenderer {

  private static final double px = 1D / 16D;

  private static float[] brightnessPerSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
    }
  }

  private static final VertexRotationFacing xform = new VertexRotationFacing(SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  private static final BoundingBox[] bb_pole = new BoundingBox[16];
  static {
    for (int i = 0; i < 16; i++) {
      bb_pole[i] = makePartialBB(7, 0, 7, 8, i + 1, 8).translate((float) (0.5 * px), 0.475f, (float) (0.5 * px));
    }
  }
  private static final BoundingBox bb_flag = makePartialBB(9, 8, 8, 16, 16, 8).translate(0, 0.475f, 0);

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    if (te instanceof TileFlag) {
      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      Tessellator.instance.startDrawingQuads();
      RenderUtil.bindBlockTexture();
      Tessellator.instance.addTranslation((float) x, (float) y, (float) z);
      renderFlag((TileFlag) te, partialTick);
      Tessellator.instance.addTranslation((float) -x, (float) -y, (float) -z);
      Tessellator.instance.draw();
      GL11.glPopAttrib();
    }
  }

  public static void renderFlag(TileFlag te, float partialTick) {
    IIcon icon = BlockFlag.blockFlag.getIcon(6, 0);
    final BlockCoord parent = te.getParent();
    final BlockCoord location = te.getLocation();
    final int renderAge = te.getRenderAge();
    final long tickCount = proxy.getTickCount();

    boolean renderFlag;
    double flagRotation, poleRotation;
    BoundingBox bb;

    // (1) set up
    int renderAgeScaled = renderAge / 4;
    if (!te.isCharged()) {
      return;
    } else if ((renderAgeScaled > 7 || renderAge < 0) && !te.getWorldObj().isAirBlock(te.xCoord, te.yCoord + 1, te.zCoord)) {
      renderAgeScaled = 7;
      bb = bb_pole[renderAgeScaled];
      renderFlag = false;
      poleRotation = flagRotation = 0;
      te.resetRenderAge(6 * 4);
      te.playSound(1);
    } else if (renderAgeScaled > 15 || renderAge < 0) {
      renderAgeScaled = 15;
      bb = bb_pole[15];
      renderFlag = true;
      if (parent != null) {
        poleRotation = flagRotation = computeRotation(location, parent);
      } else {
        long tickCountSalted = tickCount + (location.x << 16) + (location.y << 24) + (location.z << 8);
        flagRotation = (((tickCountSalted * (0.25)) % (Math.PI * 2)) + ((partialTick * (0.25)) % (Math.PI * 2))) - Math.PI;
        poleRotation = 0;
      }
    } else {
      int renderAgePartial = renderAge - renderAgeScaled * 4 - 4;
      if (renderAge == 1 && renderAgePartial == -3) {
        te.playSound(0);
      }
      bb = bb_pole[renderAgeScaled].translate(0, (float) ((renderAgePartial + partialTick) / 4 * px), 0);
      renderFlag = false;
      poleRotation = flagRotation = 0;
    }

    // (2) Render pole
    xform.setAngle(poleRotation);
    setupVertices(bb, xform);

    float minU = icon.getMinU();
    float maxU = icon.getInterpolatedU(2);
    float minV = icon.getMinV();
    float maxV = icon.getInterpolatedV(renderAgeScaled + 1);

    renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
    minU = maxU;
    maxU = icon.getInterpolatedU(4);
    renderSingleFace(ForgeDirection.EAST, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
    minU = maxU;
    maxU = icon.getInterpolatedU(6);
    renderSingleFace(ForgeDirection.WEST, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
    minU = maxU;
    maxU = icon.getInterpolatedU(8);
    renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);

    minU = icon.getInterpolatedU(2);
    maxU = icon.getInterpolatedU(4);
    minV = icon.getMinV();
    maxV = icon.getInterpolatedV(2);
    renderSingleFace(ForgeDirection.UP, minU, maxU, minV, maxV, xform, brightnessPerSide, false);

    // (3) Render flag
    if (renderFlag) {
      VertexRotation xform2 = new VertexRotation(0, new Vector3d(0, 0.5, 0), new Vector3d(1, 1.25, 0.5));
      xform2.setAngle(5 * Math.PI / 180);
      VertexTransformComposite xform3 = new VertexTransformComposite(xform2, xform);

      xform.setAngle(flagRotation);
      setupVertices(bb_flag, xform3);

      minU = icon.getInterpolatedU(8);
      maxU = icon.getMaxU();
      minV = icon.getMinV();
      maxV = icon.getInterpolatedV(8);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, (tickCount & 0b100) == 0);

      minV = icon.getInterpolatedV(8);
      maxV = icon.getMaxV();
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, (tickCount & 0b100) != 0);

      xform2.setAngle(-5 * Math.PI / 180);
      setupVertices(bb_flag, xform3);

      minU = icon.getInterpolatedU(8);
      maxU = icon.getMaxU();
      minV = icon.getMinV();
      maxV = icon.getInterpolatedV(8);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, (tickCount & 0b100) == 0);

      minV = icon.getInterpolatedV(8);
      maxV = icon.getMaxV();
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, (tickCount & 0b100) != 0);
    }

  }

  private static double computeRotation(BlockCoord from, BlockCoord to) {
    double d0 = from.x - to.x;
    double d2 = from.z - to.z;
    double f = Math.atan2(d2, d0);
    return Math.PI - f;
  }

  private static BoundingBox makePartialBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    BoundingBox bb = new BoundingBox(px * minX, px * minY, px * minZ, px * maxX, px * maxY, px * maxZ);
    return bb;
  }

}
