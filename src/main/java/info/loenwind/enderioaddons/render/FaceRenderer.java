package info.loenwind.enderioaddons.render;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector3d;

public class FaceRenderer {

  public static final float[] stdBrightness = new float[6];
  public static final float[] stdBrightnessInside = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      stdBrightness[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      stdBrightnessInside[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
    }
  }

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

  public static void renderCube(BoundingBox bb, IIcon tex, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    setupVertices(bb, xForm);
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();

      for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
        renderSingleFace(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  private final static ForgeDirection[] AROUND = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };

  public static void renderSkirt(BoundingBox bb, IIcon[] icons, double texMinU, double texMaxU, double texMinV, double texMaxV, VertexTransform xForm,
      float[] brightnessPerSide, boolean inside) {
    for (ForgeDirection face : AROUND) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        renderSingleFace(bb, face, tex, texMinU, texMaxU, texMinV, texMaxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSkirt(BoundingBox bb, IIcon tex, double texMinU, double texMaxU, double texMinV, double texMaxV, VertexTransform xForm,
      float[] brightnessPerSide, boolean inside) {
    if (tex != null) {
      for (ForgeDirection face : AROUND) {
        renderSingleFace(bb, face, tex, texMinU, texMaxU, texMinV, texMaxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSkirt(BoundingBox bb, IIcon[] icons, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    for (ForgeDirection face : AROUND) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        renderSingleFace(bb, face, tex, 0, 16, 0, 16, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSkirt(BoundingBox bb, IIcon tex, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    if (tex != null) {
      for (ForgeDirection face : AROUND) {
        renderSingleFace(bb, face, tex, 0, 16, 0, 16, xForm, brightnessPerSide, inside);
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

  public static void renderSingleFace(BoundingBox bb, ForgeDirection face, IIcon[] icons, double texMinU, double texMaxU, double texMinV, double texMaxV,
      VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    IIcon tex = icons[face.ordinal()];
    if (tex != null) {
      renderSingleFace(bb, face, tex, texMinU, texMaxU, texMinV, texMaxV, xForm, brightnessPerSide, inside);
    }
  }

  public static void renderSingleFace(BoundingBox bb, ForgeDirection face, IIcon tex, double texMinU, double texMaxU, double texMinV, double texMaxV,
      VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    setupVertices(bb, xForm);
    float minU = tex.getInterpolatedU(texMinU);
    float maxU = tex.getInterpolatedU(texMaxU);
    float minV = tex.getInterpolatedV(texMinV);
    float maxV = tex.getInterpolatedV(texMaxV);
    renderSingleFace(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
  }

  public static void renderSingleFace(BoundingBox bb, ForgeDirection face, IIcon tex, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    setupVertices(bb, xForm);
    float minU = tex.getMinU();
    float maxU = tex.getMaxU();
    float minV = tex.getMinV();
    float maxV = tex.getMaxV();
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

  public static void setLighting(int blockLight, int skylight) {
    int mixedBrightness = ((blockLight & 0b1111) << 4) | ((skylight & 0b1111) << 20);
    Tessellator.instance.setBrightness(mixedBrightness);
    FaceRenderer.world = null;
    FaceRenderer.block = null;
    FaceRenderer.bc = null;
  }

  private static final double ROTATION_AMOUNT = Math.PI / 2;

  public static ForgeDirection rotate(VertexTransform xForm, ForgeDirection dir) {
    if (xForm instanceof VertexRotationFacing) {
      double angle = ((VertexRotationFacing) xForm).getAngle();
      if (angle < ROTATION_AMOUNT * 0.5 || angle >= ROTATION_AMOUNT * 3.5) {
        return dir;
      } else if (angle >= ROTATION_AMOUNT * 0.5 && angle < ROTATION_AMOUNT * 1.5) {
        return dir.getRotation(ForgeDirection.DOWN);
      } else if (angle >= ROTATION_AMOUNT * 1.5 && angle < ROTATION_AMOUNT * 2.5) {
        return dir.getOpposite();
      } else if (angle >= ROTATION_AMOUNT * 2.5 && angle < ROTATION_AMOUNT * 3.5) {
        return dir.getRotation(ForgeDirection.UP);
      }
    }
    return dir;
  }

  public static void renderSingleFace(ForgeDirection face, float minU, float maxU, float minV, float maxV, VertexTransform xForm, float[] brightnessPerSide,
      boolean inside) {
    ForgeDirection normal = rotate(xForm, inside ? face.getOpposite() : face);
    Tessellator.instance.setNormal(normal.offsetX, normal.offsetY, normal.offsetZ);

    if (block != null && world != null && bc != null) {
      BlockCoord to = bc.getLocation(normal);
      final int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(world, to.x, to.y, to.z);
      if (mixedBrightnessForBlock == 0) {
        // avoid totally black inner faces when there's a solid block
        Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, bc.x, bc.y, bc.z));
      } else {
        Tessellator.instance.setBrightness(mixedBrightnessForBlock);
      }
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

  public static void renderCube_skewed(BoundingBox bb, IIcon[] icons, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    if (bb != null) {
      setupVertices(bb, xForm);
    }
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        float minU = tex.getMinU();
        float maxU = tex.getMaxU();
        float minV = tex.getMinV();
        float maxV = tex.getMaxV();

        renderSingleFace_skewed(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderCube_skewed(BoundingBox bb, IIcon tex, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    if (bb != null) {
      setupVertices(bb, xForm);
    }
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();
      for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
        renderSingleFace_skewed(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSkirt_skewed(BoundingBox bb, IIcon[] icons, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    if (bb != null) {
      setupVertices(bb, xForm);
    }
    for (ForgeDirection face : AROUND) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        float minU = tex.getMinU();
        float maxU = tex.getMaxU();
        float minV = tex.getMinV();
        float maxV = tex.getMaxV();

        renderSingleFace_skewed(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSkirt_skewed(BoundingBox bb, IIcon tex, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    if (bb != null) {
      setupVertices(bb, xForm);
    }
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();
      for (ForgeDirection face : AROUND) {
        renderSingleFace_skewed(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
      }
    }
  }

  public static void renderSingleFace_skewed(ForgeDirection face, IIcon[] icons, VertexTransform xForm, float[] brightnessPerSide, boolean inside) {
    IIcon tex = icons[face.ordinal()];
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();

      renderSingleFace_skewed(face, minU, maxU, minV, maxV, xForm, brightnessPerSide, inside);
    }
  }

  public static void startSkewedDrawing() {
    setupTessellatorOffsets(true);

    if (tessIsDrawing) {
      Tessellator.instance.draw();
    }

    GL11.glBegin(GL11.GL_QUADS);
  }

  public static void finishSkewedDrawing() {
    GL11.glEnd();

    if (tessIsDrawing) {
      Tessellator.instance.startDrawingQuads();
    }
  }

  public static void renderSingleFace_skewed(ForgeDirection face, float minU, float maxU, float minV, float maxV, VertexTransform xForm,
      float[] brightnessPerSide, boolean inside) {
    ForgeDirection normal = rotate(xForm, inside ? face.getOpposite() : face);
    GL11.glNormal3i(normal.offsetX, normal.offsetY, normal.offsetZ);

    if (block != null && world != null && bc != null) {
      BlockCoord to = bc.getLocation(normal);
      int brightness = block.getMixedBrightnessForBlock(world, to.x, to.y, to.z);
      if (brightness == 0) {
        // avoid totally black inner faces when there's a solid block
        brightness = block.getMixedBrightnessForBlock(world, bc.x, bc.y, bc.z);
      }
      int skyLight = brightness >>> 16;
      int blockLight = brightness & 0x0000FFFF;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, blockLight, skyLight);
    }

    if (brightnessPerSide != null) {
      byte color = (byte) (brightnessPerSide[normal.ordinal()] * 255);
      GL11.glColor4ub(color, color, color, (byte) 255);
    } else {
      GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
    }

    double w = 1;
    if (inside) {
      switch (face) {
      case NORTH:
        w = verts[1].distance(verts[0]);
        addVecWithUVW(verts[0], maxU, maxV, w);
        addVecWithUVW(verts[1], minU, maxV, w);
        w = verts[3].distance(verts[2]);
        addVecWithUVW(verts[2], minU, minV, w);
        addVecWithUVW(verts[3], maxU, minV, w);
        break;
      case SOUTH:
        w = verts[4].distance(verts[5]);
        addVecWithUVW(verts[5], maxU, maxV, w);
        addVecWithUVW(verts[4], minU, maxV, w);
        w = verts[6].distance(verts[7]);
        addVecWithUVW(verts[7], minU, minV, w);
        addVecWithUVW(verts[6], maxU, minV, w);
        break;
      case UP:
        addVecWithUVW(verts[2], maxU, minV, w);
        addVecWithUVW(verts[6], maxU, maxV, w);
        addVecWithUVW(verts[7], minU, maxV, w);
        addVecWithUVW(verts[3], minU, minV, w);
        break;
      case DOWN:
        addVecWithUVW(verts[1], maxU, minV, w);
        addVecWithUVW(verts[0], minU, minV, w);
        addVecWithUVW(verts[4], minU, maxV, w);
        addVecWithUVW(verts[5], maxU, maxV, w);
        break;
      case EAST:
        w = verts[2].distance(verts[6]);
        addVecWithUVW(verts[6], minU, minV, w);
        addVecWithUVW(verts[2], maxU, minV, w);
        w = verts[5].distance(verts[1]);
        addVecWithUVW(verts[1], maxU, maxV, w);
        addVecWithUVW(verts[5], minU, maxV, w);
        break;
      case WEST:
        w = verts[0].distance(verts[4]);
        addVecWithUVW(verts[4], maxU, maxV, w);
        addVecWithUVW(verts[0], minU, maxV, w);
        w = verts[7].distance(verts[3]);
        addVecWithUVW(verts[3], minU, minV, w);
        addVecWithUVW(verts[7], maxU, minV, w);
        break;
      default:
        break;
      }
    } else {
      switch (face) {
      case NORTH:
        w = verts[1].distance(verts[0]);
        addVecWithUVW(verts[1], minU, maxV, w); // bottom left
        addVecWithUVW(verts[0], maxU, maxV, w); // bottom right
        w = verts[3].distance(verts[2]);
        addVecWithUVW(verts[3], maxU, minV, w); // top right
        addVecWithUVW(verts[2], minU, minV, w); // top left
        break;
      case SOUTH:
        w = verts[4].distance(verts[5]);
        addVecWithUVW(verts[4], minU, maxV, w);
        addVecWithUVW(verts[5], maxU, maxV, w);
        w = verts[6].distance(verts[7]);
        addVecWithUVW(verts[6], maxU, minV, w);
        addVecWithUVW(verts[7], minU, minV, w);
        break;
      case UP:
        addVecWithUVW(verts[6], maxU, maxV, w);
        addVecWithUVW(verts[2], maxU, minV, w);
        addVecWithUVW(verts[3], minU, minV, w);
        addVecWithUVW(verts[7], minU, maxV, w);
        break;
      case DOWN:
        addVecWithUVW(verts[0], minU, minV, w);
        addVecWithUVW(verts[1], maxU, minV, w);
        addVecWithUVW(verts[5], maxU, maxV, w);
        addVecWithUVW(verts[4], minU, maxV, w);
        break;
      case EAST:
        w = verts[2].distance(verts[6]);
        addVecWithUVW(verts[2], maxU, minV, w);
        addVecWithUVW(verts[6], minU, minV, w);
        w = verts[5].distance(verts[1]);
        addVecWithUVW(verts[5], minU, maxV, w);
        addVecWithUVW(verts[1], maxU, maxV, w);
        break;
      case WEST:
        w = verts[0].distance(verts[4]);
        addVecWithUVW(verts[0], minU, maxV, w);
        addVecWithUVW(verts[4], maxU, maxV, w);
        w = verts[7].distance(verts[3]);
        addVecWithUVW(verts[7], maxU, minV, w);
        addVecWithUVW(verts[3], minU, minV, w);
      default:
        break;
      }
    }
  }

  private static Field xOffset, yOffset, zOffset, tessIsDrawingf;
  private static double xOffsetv, yOffsetv, zOffsetv;
  private static boolean tessIsDrawing;

  public static void setupTessellatorOffsets(boolean resetTessMode) {
    try {
      if (xOffset == null) {
        xOffset = Tessellator.class.getDeclaredField("xOffset");
        xOffset.setAccessible(true);
        yOffset = Tessellator.class.getDeclaredField("yOffset");
        yOffset.setAccessible(true);
        zOffset = Tessellator.class.getDeclaredField("zOffset");
        zOffset.setAccessible(true);
        tessIsDrawingf = Tessellator.class.getDeclaredField("isDrawing");
        tessIsDrawingf.setAccessible(true);
      }
      xOffsetv = xOffset.getDouble(Tessellator.instance);
      yOffsetv = yOffset.getDouble(Tessellator.instance);
      zOffsetv = zOffset.getDouble(Tessellator.instance);
      if (resetTessMode) {
        tessIsDrawing = tessIsDrawingf.getBoolean(Tessellator.instance);
      }
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private static void addVecWithUVW(Vector3d vec, float u, float v, double w) {
    GL11.glTexCoord4d(u * w, v * w, 0d, 1f * w);
    GL11.glVertex3d(vec.x + xOffsetv, vec.y + yOffsetv, vec.z + zOffsetv);
  }

}
