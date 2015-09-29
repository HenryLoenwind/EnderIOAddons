package info.loenwind.enderioaddons.gui;

import info.loenwind.enderioaddons.EnderIOAddons;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.client.render.IWidgetMap;

public enum IconEIOA implements IWidgetIcon {

  REDSTONE_MODE_RISING_EDGE(0, 0), //
  REDSTONE_MODE_FALLING_EDGE(1, 0), //
  REDSTONE_MODE_ON_HIGH(2, 0), //
  REDSTONE_MODE_ON_LOW(3, 0), //
  REDSTONE_MODE_HIGH_PULSE(4, 0), //
  REDSTONE_MODE_LOW_PULSE(5, 0), //
  ;

  // Texture size is actually 512 but everything is aligned to a 256 grid
  private static final int TEX_SIZE = 256;

  private final int x;
  private final int y;
  private final int width;
  private final int height;
  private final IconEIOA overlay;

  private static final ResourceLocation TEXTURE = new ResourceLocation(EnderIOAddons.DOMAIN + ":textures/gui/widgets.png");

  private static final IWidgetMap map = new IWidgetMap.WidgetMapImpl(TEX_SIZE, TEXTURE) {
    @Override
    public void render(IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.render(widget, x, y, width, height, zLevel, doDraw, flipY);
    }
  };

  private IconEIOA(int grid_x, int grid_y) {
    this(grid_x, grid_y, null);
  }

  private IconEIOA(int grid_x, int grid_y, IconEIOA overlay) {
    this(grid_x, grid_y, 1, 1, overlay);
  }

  private IconEIOA(int grid_x, int grid_y, int grid_width, int grid_height) {
    this(grid_x, grid_y, grid_width, grid_height, null);
  }

  private IconEIOA(int grid_x, int grid_y, int grid_width, int grid_height, IconEIOA overlay) {
    this.x = grid_x * 16;
    this.y = grid_y * 16;
    this.width = grid_width * 16;
    this.height = grid_height * 16;
    this.overlay = overlay;
  }

  @Override
  public IWidgetMap getMap() {
    return map;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public IconEIOA getOverlay() {
    return overlay;
  }
}
