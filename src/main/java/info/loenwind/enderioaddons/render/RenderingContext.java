package info.loenwind.enderioaddons.render;

import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.common.Profiler;
import info.loenwind.enderioaddons.config.Config;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.enderio.core.common.util.BlockCoord;

public class RenderingContext {

  private static Field xOffset_field, yOffset_field, zOffset_field, tessIsDrawing_field, vertexCount_field;
  private static boolean derpedTess = false;
  private static int glErrorCount = 0;

  public final double xOffset;
  public final double yOffset;
  public final double zOffset;
  private final boolean tessIsDrawing;
  private final int vertexCount;

  public final float[] blockLight = new float[ForgeDirection.values().length];
  public final float[] skyLight = new float[ForgeDirection.values().length];
  public final int[] mixedLight = new int[ForgeDirection.values().length];
  public final boolean hasLight;

  public RenderingContext() {
    hasLight = false;
    xOffset = yOffset = zOffset = 0;
    tessIsDrawing = false;
    vertexCount = 0;
  }

  public RenderingContext(IBlockAccess world, BlockCoord bc) {
    if (world != null && bc != null) {
      hasLight = true;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        BlockCoord to = bc.getLocation(dir);
        int brightness = to.getBlock(world).getMixedBrightnessForBlock(world, to.x, to.y, to.z);
        if (brightness == 0) {
          // avoid totally black inner faces when there's a solid block
          brightness = bc.getBlock(world).getMixedBrightnessForBlock(world, bc.x, bc.y, bc.z);
        }
        mixedLight[dir.ordinal()] = brightness;
        skyLight[dir.ordinal()] = brightness >>> 16;
        blockLight[dir.ordinal()] = brightness & 0x0000FFFF;
      }
    } else {
      hasLight = false;
    }

    setupTessellatorFields();
    xOffset = getDouble(xOffset_field);
    yOffset = getDouble(yOffset_field);
    zOffset = getDouble(zOffset_field);
    vertexCount = getInt(vertexCount_field);
    tessIsDrawing = getBoolean(tessIsDrawing_field);
  }

  private static double getDouble(Field field) {
    if (!derpedTess) {
      try {
        return field.getDouble(Tessellator.instance);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        Log.warn("Some mod has replaced Minecraft's tesselator. Help, without that one I don't now where to render!\n" + e);
        derpedTess = true;
      }
    }
    return 0;
  }

  private static boolean getBoolean(Field field) {
    if (!derpedTess) {
      try {
        return field.getBoolean(Tessellator.instance);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        Log.warn("Some mod has replaced Minecraft's tesselator. Help, without that one I don't now where to render!\n" + e);
        derpedTess = true;
      }
    }
    return false;
  }

  private static int getInt(Field field) {
    if (!derpedTess) {
      try {
        return field.getInt(Tessellator.instance);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        Log.warn("Some mod has replaced Minecraft's tesselator. Help, without that one I don't now where to render!\n" + e);
        derpedTess = true;
      }
    }
    return 0;
  }

  public void execute(List<CachableRenderStatement> csr, String profilerContext) {
    if (!derpedTess && Config.directDrawingEnabled.getBoolean() && glErrorCount != -1) {
      if (tessIsDrawing) {
        if (vertexCount == 0) {
          Tessellator.instance.addVertex(0, 0, 0);
          Tessellator.instance.addVertex(0, 0, 0);
          Tessellator.instance.addVertex(0, 0, 0);
          Tessellator.instance.addVertex(0, 0, 0);
        }
        Tessellator.instance.draw();
      }
      clearGLerrors();
      long id = Profiler.client.start();
      for (CachableRenderStatement statement : csr) {
        statement.execute(this);
      }
      Profiler.client.stop(id, profilerContext + " (direct)");
      if (hasGLerrors()) {
        System.out.println(lastError + " " + GLU.gluErrorString(lastError));
        glErrorCount++;
      }
      if (tessIsDrawing) {
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.addVertex(0, 0, 0);
        Tessellator.instance.addVertex(0, 0, 0);
        Tessellator.instance.addVertex(0, 0, 0);
        Tessellator.instance.addVertex(0, 0, 0);
      }
      if (glErrorCount > 0) {
        glErrorCount++;
        if (glErrorCount > 1000) {
          glErrorCount = -1;
          Log.warn("Disabled direct drawing after too many OpenGL errors. If you got rendering errors, you can"
              + " disabled direct drawing completely in the mod options. Last error was: " + lastError + " (" + GLU.gluErrorString(lastError) + ")");
          Minecraft.getMinecraft().thePlayer
              .addChatComponentMessage(new ChatComponentText(
                  "Disabled direct drawing after too many OpenGL errors. If you got rendering errors, you can disabled direct drawing completely in the mod options for Ender IO Addons."));
        }
      }
    } else {
      execute_tesselated(csr, profilerContext);
    }
  }

  public void execute_tesselated(List<CachableRenderStatement> csr, String profilerContext) {
    if (!tessIsDrawing) {
      Tessellator.instance.startDrawingQuads();
    }
    long id = Profiler.client.start();
    for (CachableRenderStatement statement : csr) {
      statement.execute_tesselated(this);
    }
    Profiler.client.stop(id, profilerContext + " (tesselator)");
    if (!tessIsDrawing) {
      Tessellator.instance.draw();
    }
  }

  public static void setupTessellatorFields() {
    if (!derpedTess) {
      try {
        if (xOffset_field == null) {
          xOffset_field = getField("xOffset", "field_78408_v");
          xOffset_field.setAccessible(true);
          yOffset_field = getField("yOffset", "field_78407_w");
          yOffset_field.setAccessible(true);
          zOffset_field = getField("zOffset", "field_78417_x");
          zOffset_field.setAccessible(true);
          tessIsDrawing_field = getField("isDrawing", "field_78415_z");
          tessIsDrawing_field.setAccessible(true);
          vertexCount_field = getField("vertexCount", "field_78406_i");
          vertexCount_field.setAccessible(true);
        }
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
        Log.warn("Some mod has replaced Minecraft's tesselator. Help, without that one I don't now where to render!\n" + e);
        String b = "";
        for (Field f : Tessellator.class.getDeclaredFields()) {
          b += " <" + f.toGenericString() + ">";
        }
        Log.warn("Fields available:" + b);
        derpedTess = true;
      }
    }
  }

  private static Field getField(String name, String alternative) throws NoSuchFieldException, SecurityException {
    Field result;
    try {
      result = Tessellator.class.getDeclaredField(name);
    } catch (NoSuchFieldException | SecurityException e) {
      result = Tessellator.class.getDeclaredField(alternative);
    }
    return result;
  }

  public boolean clearGLerrors() {
    for (int i = 0; i < 10; i++) {
      if ((lastError = GL11.glGetError()) == GL11.GL_NO_ERROR) {
        return true;
      }
    }
    return false;
  }

  int lastError;

  public boolean hasGLerrors() {
    return (lastError = GL11.glGetError()) != GL11.GL_NO_ERROR;
  }

}