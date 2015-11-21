package info.loenwind.enderioaddons.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import crazypants.enderio.EnderIO;

public class TickRotator {

  private final Timer timer;

  private TickRotator() {
    timer = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer");
  }

  public void rotateGl(double speed, double x, double y, double z) {
    double rot = EnderIO.proxy.getTickCount() * speed;
    GL11.glRotated(rot, x, y, z);
    if (timer != null) {
      rot = timer.renderPartialTicks * speed;
      GL11.glRotated(rot, x, y, z);
    }
  }

  public void rotateGl(double speed, ForgeDirection axis) {
    rotateGl(speed, axis.offsetX, axis.offsetY, axis.offsetZ);
  }

  public void rotateGl(double speed) {
    rotateGl(speed, 0, 1, 0);
  }

  public void rotateGl() {
    rotateGl(1, 0, 1, 0);
  }

  private static TickRotator instance = null;

  public static TickRotator getInstance() {
    if (instance == null) {
      instance = new TickRotator();
    }
    return instance;
  }

}
