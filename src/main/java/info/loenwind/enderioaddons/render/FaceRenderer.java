package info.loenwind.enderioaddons.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector3d;

public class FaceRenderer {

  private FaceRenderer() {
  }

  public static final Vector3d[] verts = new Vector3d[8];
  static {
    for (int i = 0; i < verts.length; i++) {
      verts[i] = new Vector3d();
    }
  }

  public static void setupVertices(BoundingBox bound, VertexTransform xForm) {
    verts[0].set(bound.minX, bound.minY, bound.minZ);
    verts[1].set(bound.maxX, bound.minY, bound.minZ);
    verts[2].set(bound.maxX, bound.maxY, bound.minZ);
    verts[3].set(bound.minX, bound.maxY, bound.minZ);
    verts[4].set(bound.minX, bound.minY, bound.maxZ);
    verts[5].set(bound.maxX, bound.minY, bound.maxZ);
    verts[6].set(bound.maxX, bound.maxY, bound.maxZ);
    verts[7].set(bound.minX, bound.maxY, bound.maxZ);

    if (xForm != null) {
      for (Vector3d vec : verts) {
        xForm.apply(vec);
      }
    }
  }

  public static void addVecWithUV(Vector3d vec, double u, double v) {
    Tessellator.instance.addVertexWithUV(vec.x, vec.y, vec.z, u, v);
  }

  public static void renderSingleFace(BoundingBox bb, ForgeDirection face, IIcon tex, int texMinU, int texMaxU, int texMinV, int texMaxV,
      VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    setupVertices(bb, xForm);
    float minU = tex.getMinU();
    float maxU = tex.getMaxU();
    float minV = tex.getMinV();
    float maxV = tex.getMaxV();
    float sizeU = maxU - minU;
    float sizeV = maxV - minV;
    minU += texMinU * sizeU / 16.0f;
    maxU -= texMaxU * sizeU / 16.0f;
    minV += texMinV * sizeV / 16.0f;
    maxV -= texMaxV * sizeV / 16.0f;

    if (brightnessPerSide != null) {
      float cm = brightnessPerSide[face.ordinal()];
      Tessellator.instance.setColorOpaque_F(cm, cm, cm);
    }

    if (inside) {
      switch (face) {
      case NORTH:
        Tessellator.instance.setNormal(0, 0, 1);
        addVecWithUV(verts[0], minU, maxV);
        addVecWithUV(verts[1], maxU, maxV);
        addVecWithUV(verts[2], maxU, minV);
        addVecWithUV(verts[3], minU, minV);
      case SOUTH:
        Tessellator.instance.setNormal(0, 0, -1);
        addVecWithUV(verts[5], minU, maxV);
        addVecWithUV(verts[4], maxU, maxV);
        addVecWithUV(verts[7], maxU, minV);
        addVecWithUV(verts[6], minU, minV);
      case UP:
        Tessellator.instance.setNormal(0, -1, 0);
        addVecWithUV(verts[2], minU, minV);
        addVecWithUV(verts[6], minU, maxV);
        addVecWithUV(verts[7], maxU, maxV);
        addVecWithUV(verts[3], maxU, minV);
      case DOWN:
        Tessellator.instance.setNormal(0, 1, 0);
        addVecWithUV(verts[1], maxU, maxV);
        addVecWithUV(verts[0], minU, maxV);
        addVecWithUV(verts[4], minU, minV);
        addVecWithUV(verts[5], maxU, minV);
      case EAST:
        Tessellator.instance.setNormal(-1, 0, 0);
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[2], maxU, minV);
        addVecWithUV(verts[1], maxU, maxV);
        addVecWithUV(verts[5], minU, maxV);
      case WEST:
        Tessellator.instance.setNormal(1, 0, 0);
        addVecWithUV(verts[4], minU, maxV);
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[3], maxU, minV);
        addVecWithUV(verts[7], minU, minV);
      default:
        break;
      }
    } else {
      switch (face) {
      case NORTH:
        Tessellator.instance.setNormal(0, 0, -1);
        addVecWithUV(verts[1], minU, maxV);
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[3], maxU, minV);
        addVecWithUV(verts[2], minU, minV);
      case SOUTH:
        Tessellator.instance.setNormal(0, 0, 1);
        addVecWithUV(verts[4], minU, maxV);
        addVecWithUV(verts[5], maxU, maxV);
        addVecWithUV(verts[6], maxU, minV);
        addVecWithUV(verts[7], minU, minV);
      case UP:
        Tessellator.instance.setNormal(0, 1, 0);
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[2], minU, maxV);
        addVecWithUV(verts[3], maxU, maxV);
        addVecWithUV(verts[7], maxU, minV);
      case DOWN:
        Tessellator.instance.setNormal(0, -1, 0);
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[1], minU, maxV);
        addVecWithUV(verts[5], minU, minV);
        addVecWithUV(verts[4], maxU, minV);
      case EAST:
        Tessellator.instance.setNormal(1, 0, 0);
        addVecWithUV(verts[2], minU, minV);
        addVecWithUV(verts[6], maxU, minV);
        addVecWithUV(verts[5], maxU, maxV);
        addVecWithUV(verts[1], minU, maxV);
      case WEST:
        Tessellator.instance.setNormal(-1, 0, 0);
        addVecWithUV(verts[0], minU, maxV);
        addVecWithUV(verts[4], maxU, maxV);
        addVecWithUV(verts[7], maxU, minV);
        addVecWithUV(verts[3], minU, minV);
      default:
        break;
      }
    }
  }

}
