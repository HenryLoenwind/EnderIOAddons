package info.loenwind.enderioaddons.render;

import info.loenwind.enderioaddons.machine.afarm.BlockAfarm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;

public class OverlayRenderer {

  private static float[] brightnessPerSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = 1f;
    }
  }

  private static BoundingBox shell = BoundingBox.UNIT_CUBE.scale(1.002, 1.002, 1.002);

  public static <T extends AbstractMachineEntity> boolean renderOverlays(IBlockAccess world, int x, int y, int z, @Nullable BoundingBox bb,
      @Nullable IIcon overrideTexture, AbstractMachineBlock<T> block, @Nonnull Class<T> clazz) {
    return renderOverlays(world, x, y, z, bb, overrideTexture, block, clazz, false);
  }

  @SuppressWarnings("null")
  public static <T extends AbstractMachineEntity> boolean renderOverlays(IBlockAccess world, int x, int y, int z, @Nullable BoundingBox bb,
      @Nullable IIcon overrideTexture, AbstractMachineBlock<T> block, @Nonnull Class<T> clazz, boolean addTranslation) {
    if (overrideTexture != null) {
      return renderOverlays(world, x, y, z, bb, overrideTexture, block, (T) null, addTranslation);
    } else if (world != null) {
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (clazz.isInstance(tileEntity)) {
        return renderOverlays(world, x, y, z, bb, overrideTexture, block, (T) tileEntity, addTranslation);
      }
    }
    return false;
  }

  public static <T extends AbstractMachineEntity> boolean renderOverlays(IBlockAccess world, int x, int y, int z, @Nullable BoundingBox bb,
      @Nullable IIcon overrideTexture, AbstractMachineBlock<T> block, @Nullable T te) {
    return renderOverlays(world, x, y, z, bb, overrideTexture, block, te, false);
  }

  public static <T extends AbstractMachineEntity> boolean renderOverlays(IBlockAccess world, int x, int y, int z, @Nullable BoundingBox bb,
      @Nullable IIcon overrideTexture, AbstractMachineBlock<T> block, @Nullable T te, boolean addTranslation) {
    if (overrideTexture != null) {
      if (addTranslation) {
        Tessellator.instance.addTranslation(x, y, z);
      }
      FaceRenderer.setLightingReference(world, BlockAfarm.blockAfarm, x, y, z);
      FaceRenderer.renderCube(bb == null ? shell : bb, overrideTexture, null, brightnessPerSide, false);
      FaceRenderer.clearLightingReference();
      if (addTranslation) {
        Tessellator.instance.addTranslation(-x, -y, -z);
      }
      return true;
    } else if (te != null) {
      IIcon[] icons = null;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        final IIcon icon = block.getOverlayIconForMode(te, dir, te.getIoMode(dir));
        if (icon != null) {
          if (icons == null) {
            icons = new IIcon[ForgeDirection.values().length];
          }
          icons[dir.ordinal()] = icon;
        }
      }
      if (icons != null) {
        if (addTranslation) {
          Tessellator.instance.addTranslation(x, y, z);
        }
        FaceRenderer.setLightingReference(world, BlockAfarm.blockAfarm, x, y, z);
        FaceRenderer.renderCube(bb == null ? shell : bb, icons, null, brightnessPerSide, false);
        FaceRenderer.clearLightingReference();
        if (addTranslation) {
          Tessellator.instance.addTranslation(-x, -y, -z);
        }
      }
    }
    return false;
  }

}
