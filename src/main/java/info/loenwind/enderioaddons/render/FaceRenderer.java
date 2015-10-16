package info.loenwind.enderioaddons.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector3d;

public class FaceRenderer {

  // @formatter:off
  public static int[][] sideAndFacingToSpriteOffset = new int[][] {
  //  D  U  N  S  W  E <- te.facing; v- physical side => logical side
    { 3, 2, 0, 0, 0, 0 }, // D
    { 2, 3, 1, 1, 1, 1 }, // U
    { 1, 1, 3, 2, 4, 5 }, // N
    { 0, 0, 2, 3, 5, 4 }, // S
    { 4, 5, 5, 4, 3, 2 }, // W
    { 5, 4, 4, 5, 2, 3 }  // E
  };
  // @formatter:on

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

  public static void renderCube(BoundingBox bb, IIcon[] icons, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    setupVertices(bb, xForm);
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        float minU = tex.getMinU();
        float maxU = tex.getMaxU();
        float minV = tex.getMinV();
        float maxV = tex.getMaxV();

        renderSingleFace(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  private final static ForgeDirection[] AROUND = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };

  public static void renderSkirt(BoundingBox bb, IIcon[] icons, int texMinU, int texMaxU, int texMinV, int texMaxV, VertexTransform xForm,
      float[] brightnessPerSide, boolean inside) {
    for (ForgeDirection face : AROUND) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        renderSingleFace(bb, face, tex, texMinU, texMaxU, texMinV, texMaxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSkirt(BoundingBox bb, IIcon tex, int texMinU, int texMaxU, int texMinV, int texMaxV, VertexTransform xForm,
      float[] brightnessPerSide, boolean inside) {
    if (tex != null) {
      for (ForgeDirection face : AROUND) {
        renderSingleFace(bb, face, tex, texMinU, texMaxU, texMinV, texMaxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSingleFace(BoundingBox bb, ForgeDirection face, IIcon[] icons, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    setupVertices(bb, xForm);
    IIcon tex = icons[face.ordinal()];
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();

      renderSingleFace(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
    }
  }

  public static void renderSingleFace(BoundingBox bb, ForgeDirection face, IIcon[] icons, int texMinU, int texMaxU, int texMinV, int texMaxV,
      VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    IIcon tex = icons[face.ordinal()];
    if (tex != null) {
      renderSingleFace(bb, face, tex, texMinU, texMaxU, texMinV, texMaxV, xForm, brightnessPerSide, inside);
    }
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
    minU += texMinU / 16.0f * sizeU;
    maxU -= (16.0f - texMaxU) / 16.0f * sizeU;
    minV += (16.0f - texMaxV) / 16.0f * sizeV;
    maxV -= texMinV / 16.0f * sizeV;

    renderSingleFace(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
  }

  private static Block block;
  private static IBlockAccess world;
  private static BlockCoord bc;

  public static void setLightingReference(IBlockAccess world, Block block, BlockCoord bc) {
    FaceRenderer.world = world;
    FaceRenderer.block = block;
    FaceRenderer.bc = bc;
  }

  public static void setLightingReference(IBlockAccess world, Block block, int x, int y, int z) {
    FaceRenderer.world = world;
    FaceRenderer.block = block;
    FaceRenderer.bc = new BlockCoord(x, y, z);
  }

  public static void clearLightingReference() {
    FaceRenderer.world = null;
    FaceRenderer.block = null;
    FaceRenderer.bc = null;
  }

  public static void renderSingleFace(ForgeDirection face, float minU, float maxU, float minV, float maxV, VertexTransform xForm, float[] brightnessPerSide,
      boolean inside) {
    ForgeDirection normal = inside ? face.getOpposite() : face;
    if (xForm instanceof VertexRotationFacing) {
      normal = ((VertexRotationFacing) xForm).rotate(normal);
    }
    Tessellator.instance.setNormal(normal.offsetX, normal.offsetY, normal.offsetZ);

    if (block != null && world != null && bc != null) {
      BlockCoord to = bc.getLocation(normal);
      Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, to.x, to.y, to.z));
    }

    if (brightnessPerSide != null) {
      float cm = brightnessPerSide[normal.ordinal()];
      Tessellator.instance.setColorOpaque_F(cm, cm, cm);
    }

    if (inside) {
      switch (face) {
      case NORTH:
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[1], minU, maxV);
        addVecWithUV(verts[2], minU, minV);
        addVecWithUV(verts[3], maxU, minV);
        break;
      case SOUTH:
        addVecWithUV(verts[5], maxU, maxV);
        addVecWithUV(verts[4], minU, maxV);
        addVecWithUV(verts[7], minU, minV);
        addVecWithUV(verts[6], maxU, minV);
        break;
      case UP:
        addVecWithUV(verts[2], maxU, minV);
        addVecWithUV(verts[6], maxU, maxV);
        addVecWithUV(verts[7], minU, maxV);
        addVecWithUV(verts[3], minU, minV);
        break;
      case DOWN:
        addVecWithUV(verts[1], maxU, minV);
        addVecWithUV(verts[0], minU, minV);
        addVecWithUV(verts[4], minU, maxV);
        addVecWithUV(verts[5], maxU, maxV);
        break;
      case EAST:
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[2], maxU, minV);
        addVecWithUV(verts[1], maxU, maxV);
        addVecWithUV(verts[5], minU, maxV);
        break;
      case WEST:
        addVecWithUV(verts[4], maxU, maxV);
        addVecWithUV(verts[0], minU, maxV);
        addVecWithUV(verts[3], minU, minV);
        addVecWithUV(verts[7], maxU, minV);
        break;
      default:
        break;
      }
    } else {
      switch (face) {
      case NORTH:
        addVecWithUV(verts[1], minU, maxV);
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[3], maxU, minV);
        addVecWithUV(verts[2], minU, minV);
        break;
      case SOUTH:
        addVecWithUV(verts[4], minU, maxV);
        addVecWithUV(verts[5], maxU, maxV);
        addVecWithUV(verts[6], maxU, minV);
        addVecWithUV(verts[7], minU, minV);
        break;
      case UP:
        addVecWithUV(verts[6], maxU, maxV);
        addVecWithUV(verts[2], maxU, minV);
        addVecWithUV(verts[3], minU, minV);
        addVecWithUV(verts[7], minU, maxV);
        break;
      case DOWN:
        addVecWithUV(verts[0], minU, minV);
        addVecWithUV(verts[1], maxU, minV);
        addVecWithUV(verts[5], maxU, maxV);
        addVecWithUV(verts[4], minU, maxV);
        break;
      case EAST:
        addVecWithUV(verts[2], maxU, minV);
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[5], minU, maxV);
        addVecWithUV(verts[1], maxU, maxV);
        break;
      case WEST:
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
