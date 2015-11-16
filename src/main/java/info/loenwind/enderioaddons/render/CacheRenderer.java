package info.loenwind.enderioaddons.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;

public class CacheRenderer {

  private static final ForgeDirection[] AROUND = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };
  private static final double ROTATION_AMOUNT = Math.PI / 2;

  private final List<CachableRenderStatement> csr = new ArrayList<>();
  private BoundingBox bb = null;
  private final Vector3d[] verts = new Vector3d[8];
  private boolean doLighting = true;
  private VertexTransform xForm = null;
  private float[] brightnessPerSide = null;
  private float brightness_TL = 1.0f;
  private float brightness_TR = 1.0f;
  private float brightness_BL = 1.0f;
  private float brightness_BR = 1.0f;

  public CacheRenderer copy() {
    CacheRenderer o = new CacheRenderer();
    o.csr.clear();
    o.csr.addAll(csr);
    for (int i = 0; i < verts.length; i++) {
      o.verts[i].set(verts[i].x, verts[i].y, verts[i].z);
    }
    o.doLighting = doLighting;
    o.xForm = xForm;
    o.brightnessPerSide = brightnessPerSide;
    o.brightness_TL = brightness_TL;
    o.brightness_TR = brightness_TR;
    o.brightness_BL = brightness_BL;
    o.brightness_BR = brightness_BR;
    return o;
  }

  public CacheRenderer() {
    csr.add(new CRS_start());

    for (int i = 0; i < verts.length; i++) {
      verts[i] = new Vector3d();
    }
  }

  public CacheRenderer setBB(BoundingBox bb) {
    this.bb = bb;
    calculateVerts();
    return this;
  }

  private void calculateVerts() {
    verts[0].set(bb.minX, bb.minY, bb.minZ);
    verts[1].set(bb.maxX, bb.minY, bb.minZ);
    verts[2].set(bb.maxX, bb.maxY, bb.minZ);
    verts[3].set(bb.minX, bb.maxY, bb.minZ);
    verts[4].set(bb.minX, bb.minY, bb.maxZ);
    verts[5].set(bb.maxX, bb.minY, bb.maxZ);
    verts[6].set(bb.maxX, bb.maxY, bb.maxZ);
    verts[7].set(bb.minX, bb.maxY, bb.maxZ);
  }

  public CacheRenderer setXform(VertexTransform xForm) {
    calculateVerts();
    this.xForm = xForm;
    for (Vector3d vec : verts) {
      xForm.apply(vec);
    }
    return this;
  }

  public CacheRenderer setLighting(boolean doLighting) {
    this.doLighting = doLighting;
    return this;
  }

  public CacheRenderer setLighting(int mixedBrightness, float u, float v) {
    this.doLighting = false;
    csr.add(new CRS_Light(mixedBrightness, u, v));
    return this;
  }

  public List<CachableRenderStatement> finishDrawing() {
    csr.add(new CRS_end());
    return csr;
  }

  public CacheRenderer setBrightnessPerSide(float[] brightnessPerSide) {
    this.brightnessPerSide = brightnessPerSide;
    return this;
  }

  public CacheRenderer setBrightness(float brightness_TL, float brightness_TR, float brightness_BL, float brightness_BR) {
    this.brightness_TL = brightness_TL;
    this.brightness_TR = brightness_TR;
    this.brightness_BL = brightness_BL;
    this.brightness_BR = brightness_BR;
    return this;
  }

  public CacheRenderer setBrightness(float brightness_T, float brightness_B) {
    this.brightness_TL = brightness_T;
    this.brightness_TR = brightness_T;
    this.brightness_BL = brightness_B;
    this.brightness_BR = brightness_B;
    return this;
  }

  public CacheRenderer setBrightness(float brightness) {
    this.brightness_TL = brightness;
    this.brightness_TR = brightness;
    this.brightness_BL = brightness;
    this.brightness_BR = brightness;
    return this;
  }

  public void addSingleFace(ForgeDirection face, float minU, float maxU, float minV, float maxV, boolean inside) {
    ForgeDirection aa_normal = rotate(xForm, inside ? face.getOpposite() : face);

    if (doLighting) {
      csr.add(new CRS_Lightmap(aa_normal));
    }

    byte color_TL, color_TR, color_BL, color_BR;

    if (brightnessPerSide != null) {
      color_TL = (byte) (brightnessPerSide[aa_normal.ordinal()] * brightness_TL * 255);
      color_TR = (byte) (brightnessPerSide[aa_normal.ordinal()] * brightness_TR * 255);
      color_BL = (byte) (brightnessPerSide[aa_normal.ordinal()] * brightness_BL * 255);
      color_BR = (byte) (brightnessPerSide[aa_normal.ordinal()] * brightness_BR * 255);
    } else {
      color_TL = (byte) (brightness_TL * 255);
      color_TR = (byte) (brightness_TR * 255);
      color_BL = (byte) (brightness_BL * 255);
      color_BR = (byte) (brightness_BR * 255);
    }

    if (inside) {
      switch (face) {
      case NORTH:
        csr.add(new CRS_Normal(verts[0], verts[1], verts[2]));
        csr.add(new CRS_Vertex(verts[0], maxU, maxV, verts[1].distance(verts[0]), color_BR));
        csr.add(new CRS_Vertex(verts[1], minU, maxV, verts[1].distance(verts[0]), color_BL));
        csr.add(new CRS_Vertex(verts[2], minU, minV, verts[3].distance(verts[2]), color_TL));
        csr.add(new CRS_Vertex(verts[3], maxU, minV, verts[3].distance(verts[2]), color_TR));
        break;
      case SOUTH:
        csr.add(new CRS_Normal(verts[5], verts[4], verts[7]));
        csr.add(new CRS_Vertex(verts[5], maxU, maxV, verts[4].distance(verts[5]), color_BR));
        csr.add(new CRS_Vertex(verts[4], minU, maxV, verts[4].distance(verts[5]), color_BL));
        csr.add(new CRS_Vertex(verts[7], minU, minV, verts[6].distance(verts[7]), color_TL));
        csr.add(new CRS_Vertex(verts[6], maxU, minV, verts[6].distance(verts[7]), color_TR));
        break;
      case UP:
        csr.add(new CRS_Normal(verts[2], verts[6], verts[7]));
        csr.add(new CRS_Vertex(verts[2], maxU, minV, 1, color_TL));
        csr.add(new CRS_Vertex(verts[6], maxU, maxV, 1, color_TL));
        csr.add(new CRS_Vertex(verts[7], minU, maxV, 1, color_TL));
        csr.add(new CRS_Vertex(verts[3], minU, minV, 1, color_TL));
        break;
      case DOWN:
        csr.add(new CRS_Normal(verts[1], verts[0], verts[4]));
        csr.add(new CRS_Vertex(verts[1], maxU, minV, 1, color_BL));
        csr.add(new CRS_Vertex(verts[0], minU, minV, 1, color_BL));
        csr.add(new CRS_Vertex(verts[4], minU, maxV, 1, color_BL));
        csr.add(new CRS_Vertex(verts[5], maxU, maxV, 1, color_BL));
        break;
      case EAST:
        csr.add(new CRS_Normal(verts[1], verts[5], verts[6]));
        csr.add(new CRS_Vertex(verts[1], maxU, maxV, verts[5].distance(verts[1]), color_BR));
        csr.add(new CRS_Vertex(verts[5], minU, maxV, verts[5].distance(verts[1]), color_BL));
        csr.add(new CRS_Vertex(verts[6], minU, minV, verts[2].distance(verts[6]), color_TL));
        csr.add(new CRS_Vertex(verts[2], maxU, minV, verts[2].distance(verts[6]), color_TR));
        break;
      case WEST:
        csr.add(new CRS_Normal(verts[4], verts[0], verts[3]));
        csr.add(new CRS_Vertex(verts[4], maxU, maxV, verts[0].distance(verts[4]), color_BR));
        csr.add(new CRS_Vertex(verts[0], minU, maxV, verts[0].distance(verts[4]), color_BL));
        csr.add(new CRS_Vertex(verts[3], minU, minV, verts[7].distance(verts[3]), color_TL));
        csr.add(new CRS_Vertex(verts[7], maxU, minV, verts[7].distance(verts[3]), color_TR));
        break;
      default:
        break;
      }
    } else {
      switch (face) {
      case NORTH:
        csr.add(new CRS_Normal(verts[1], verts[0], verts[3]));
        csr.add(new CRS_Vertex(verts[1], minU, maxV, verts[1].distance(verts[0]), color_BL));
        csr.add(new CRS_Vertex(verts[0], maxU, maxV, verts[1].distance(verts[0]), color_BR));
        csr.add(new CRS_Vertex(verts[3], maxU, minV, verts[3].distance(verts[2]), color_TR));
        csr.add(new CRS_Vertex(verts[2], minU, minV, verts[3].distance(verts[2]), color_TL));
        break;
      case SOUTH:
        csr.add(new CRS_Normal(verts[4], verts[5], verts[6]));
        csr.add(new CRS_Vertex(verts[4], minU, maxV, verts[4].distance(verts[5]), color_BL));
        csr.add(new CRS_Vertex(verts[5], maxU, maxV, verts[4].distance(verts[5]), color_BR));
        csr.add(new CRS_Vertex(verts[6], maxU, minV, verts[6].distance(verts[7]), color_TR));
        csr.add(new CRS_Vertex(verts[7], minU, minV, verts[6].distance(verts[7]), color_TL));
        break;
      case UP:
        csr.add(new CRS_Normal(verts[6], verts[2], verts[3]));
        csr.add(new CRS_Vertex(verts[6], maxU, maxV, 1, color_TL));
        csr.add(new CRS_Vertex(verts[2], maxU, minV, 1, color_TL));
        csr.add(new CRS_Vertex(verts[3], minU, minV, 1, color_TL));
        csr.add(new CRS_Vertex(verts[7], minU, maxV, 1, color_TL));
        break;
      case DOWN:
        csr.add(new CRS_Normal(verts[0], verts[1], verts[5]));
        csr.add(new CRS_Vertex(verts[0], minU, minV, 1, color_BL));
        csr.add(new CRS_Vertex(verts[1], maxU, minV, 1, color_BL));
        csr.add(new CRS_Vertex(verts[5], maxU, maxV, 1, color_BL));
        csr.add(new CRS_Vertex(verts[4], minU, maxV, 1, color_BL));
        break;
      case EAST:
        csr.add(new CRS_Normal(verts[5], verts[1], verts[2]));
        csr.add(new CRS_Vertex(verts[5], minU, maxV, verts[5].distance(verts[1]), color_BL));
        csr.add(new CRS_Vertex(verts[1], maxU, maxV, verts[5].distance(verts[1]), color_BR));
        csr.add(new CRS_Vertex(verts[2], maxU, minV, verts[2].distance(verts[6]), color_TR));
        csr.add(new CRS_Vertex(verts[6], minU, minV, verts[2].distance(verts[6]), color_TL));
        break;
      case WEST:
        csr.add(new CRS_Normal(verts[0], verts[4], verts[7]));
        csr.add(new CRS_Vertex(verts[0], minU, maxV, verts[0].distance(verts[4]), color_BL));
        csr.add(new CRS_Vertex(verts[4], maxU, maxV, verts[0].distance(verts[4]), color_BR));
        csr.add(new CRS_Vertex(verts[7], maxU, minV, verts[7].distance(verts[3]), color_TR));
        csr.add(new CRS_Vertex(verts[3], minU, minV, verts[7].distance(verts[3]), color_TL));
        break;
      default:
        break;
      }
    }
  }

  public CacheRenderer addSide(ForgeDirection face, IIcon[] icons, boolean inside) {
    IIcon tex = icons[face.ordinal()];
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();

      addSingleFace(face, minU, maxU, minV, maxV, inside);
    }
    return this;
  }

  public CacheRenderer addSide(ForgeDirection face, IIcon tex, boolean inside) {
    if (tex != null) {
      float minU = tex.getMinU();
      float maxU = tex.getMaxU();
      float minV = tex.getMinV();
      float maxV = tex.getMaxV();

      addSingleFace(face, minU, maxU, minV, maxV, inside);
    }
    return this;
  }

  public void addSkirt(IIcon[] icons, boolean inside) {
    addSides(AROUND, icons, inside);
  }

  public void addSkirt(IIcon tex, boolean inside) {
    addSides(AROUND, tex, inside);
  }

  public void addAllSides(IIcon[] icons, boolean inside) {
    addSides(ForgeDirection.VALID_DIRECTIONS, icons, inside);
  }

  public void addAllSides(IIcon tex, boolean inside) {
    addSides(ForgeDirection.VALID_DIRECTIONS, tex, inside);
  }

  public void addSides(ForgeDirection[] sides, IIcon[] icons, boolean inside) {
    for (ForgeDirection face : sides) {
      IIcon tex = icons[face.ordinal()];
      if (tex != null) {
        float minU = tex.getMinU();
        float maxU = tex.getMaxU();
        float minV = tex.getMinV();
        float maxV = tex.getMaxV();
        addSingleFace(face, minU, maxU, minV, maxV, inside);
      }
    }
  }

  public void addSides(ForgeDirection[] sides, IIcon tex, boolean inside) {
    if (tex != null) {
      for (ForgeDirection face : sides) {
        float minU = tex.getMinU();
        float maxU = tex.getMaxU();
        float minV = tex.getMinV();
        float maxV = tex.getMaxV();
        addSingleFace(face, minU, maxU, minV, maxV, inside);
      }
    }
  }

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

  /////////////////////////////////////////////////////////////////////
  // classes
  /////////////////////////////////////////////////////////////////////

  private static class CRS_start implements CachableRenderStatement {
    @Override
    public void execute(RenderingContext renderingContext) {
      GL11.glShadeModel(GL11.GL_SMOOTH);
      GL11.glBegin(GL11.GL_QUADS);
    }

    @Override
    public void execute_tesselated(RenderingContext renderingContext) {
    }

    @Override
    public String toString() {
      return "CRS_start []";
    }

  }

  private static class CRS_end implements CachableRenderStatement {
    @Override
    public void execute(RenderingContext renderingContext) {
      GL11.glEnd();
      if (Minecraft.isAmbientOcclusionEnabled()) {
        GL11.glShadeModel(GL11.GL_SMOOTH);
      } else {
        GL11.glShadeModel(GL11.GL_FLAT);
      }
    }

    @Override
    public void execute_tesselated(RenderingContext renderingContext) {
    }

    @Override
    public String toString() {
      return "CRS_end []";
    }

  }

  private static class CRS_Lightmap implements CachableRenderStatement {
    private final int normal;

    public CRS_Lightmap(ForgeDirection normal) {
      this.normal = normal.ordinal();
    }
    @Override
    public void execute(RenderingContext renderingContext) {
      if (renderingContext.hasLight) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, renderingContext.blockLight[normal], renderingContext.skyLight[normal]);
      }
    }

    @Override
    public void execute_tesselated(RenderingContext renderingContext) {
      if (renderingContext.hasLight) {
        Tessellator.instance.setBrightness(renderingContext.mixedLight[normal]);
      }
    }

    @Override
    public String toString() {
      return "CRS_Lightmap [normal=" + ForgeDirection.values()[normal] + "]";
    }

  }

  private static class CRS_Light implements CachableRenderStatement {
    private final int mixedBrightness;
    private final float u, v;

    public CRS_Light(int mixedBrightness, float u, float v) {
      this.mixedBrightness = mixedBrightness;
      this.u = u;
      this.v = v;
    }

    @Override
    public void execute(RenderingContext renderingContext) {
      if (renderingContext.hasLight) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, u, v);
      }
    }

    @Override
    public void execute_tesselated(RenderingContext renderingContext) {
      if (renderingContext.hasLight) {
        Tessellator.instance.setBrightness(mixedBrightness);
      }
    }

    @Override
    public String toString() {
      return "CRS_Light [mixedBrightness=" + mixedBrightness + ", u=" + u + ", v=" + v + "]";
    }

  }

  private static class CRS_Normal implements CachableRenderStatement {
    private final double x, y, z;
    private static final Vector3d T1 = new Vector3d();
    private static final Vector3d T2 = new Vector3d();
    private static final Vector3d T3 = new Vector3d();

    public CRS_Normal(Vector3d A, Vector3d B, Vector3d C) {
      T1.set(B);
      T1.sub(A);
      T2.set(C);
      T2.sub(A);
      T3.cross(T1, T2);
      T3.normalize();
      this.x = T3.x;
      this.y = T3.y;
      this.z = T3.z;
    }

    @Override
    public void execute(RenderingContext renderingContext) {
      GL11.glNormal3d(x, y, z);
    }

    @Override
    public void execute_tesselated(RenderingContext renderingContext) {
      Tessellator.instance.setNormal((float) x, (float) y, (float) z);
    }

    @Override
    public String toString() {
      return "CRS_Normal [normal=" + x + "/" + y + "/" + z + "]";
    }

  }

  private static class CRS_Vertex implements CachableRenderStatement {
    private final double u, v, w, x, y, z;
    private final byte color;

    public CRS_Vertex(Vector3d vec, float u, float v, double w, byte color) {
      this.u = u * w;
      this.v = v * w;
      this.w = w;
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
      this.color = color;
    }

    @Override
    public void execute(RenderingContext renderingContext) {
      GL11.glColor4ub(color, color, color, (byte) 255);
      GL11.glTexCoord4d(u, v, 0d, w);
      GL11.glVertex3d(x + renderingContext.xOffset, y + renderingContext.yOffset, z + renderingContext.zOffset);
    }

    @Override
    public void execute_tesselated(RenderingContext renderingContext) {
      Tessellator.instance.func_154352_a(color, color, color);
      Tessellator.instance.addVertexWithUV(x, y, z, u / w, v / w);
    }

    @Override
    public String toString() {
      return "CRS_Vertex [u=" + u + ", v=" + v + ", w=" + w + ", x=" + x + ", y=" + y + ", z=" + z + ", color=" + (color & 0xff) + "]";
    }

  }

  @Override
  public String toString() {
    return "CacheRenderer [brightnessPerSide=" + Arrays.toString(brightnessPerSide) + ", brightness_TL=" + brightness_TL + ", brightness_TR=" + brightness_TR
        + ", brightness_BL=" + brightness_BL + ", brightness_BR=" + brightness_BR + ", csr=" + csr + "]";
  }

}
