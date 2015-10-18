package info.loenwind.enderioaddons.machine.tcom;

import static info.loenwind.enderioaddons.common.NullHelper.notnullJ;
import static info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot.FRONT_LEFT;
import static info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot.FRONT_RIGHT;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.COBBLE;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.DIAMOND;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.GOLD;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.IRON;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.LEATHER;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STICK;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STRING;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.WOOD;
import static info.loenwind.enderioaddons.render.FaceRenderer.renderCube;
import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import info.loenwind.enderioaddons.machine.framework.GroupObjectWithIcon;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.tcom.engine.EngineTcom;
import info.loenwind.enderioaddons.machine.tcom.engine.Mats;
import info.loenwind.enderioaddons.render.FaceRenderer;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RendererTcom implements ISimpleBlockRenderingHandler {

  private static float[] brightnessPerSide = new float[6];
  private static float[] brightnessPerInSide = new float[6];
  static {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      brightnessPerInSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
    }
  }

  private static final VertexRotationFacing xform = new VertexRotationFacing(SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  @Nonnull
  private final RendererFrameworkMachine frameRenderer;

  public RendererTcom(@Nonnull RendererFrameworkMachine frameRenderer) {
    this.frameRenderer = frameRenderer;
    @SuppressWarnings("null")
    GroupObject controllerPassive = new GroupObjectWithIcon(frameRenderer.getControllerPart(4), BlockTcom.blockTcom);

    frameRenderer.registerController(BlockTcom.blockTcom.getControllerModelName(), controllerPassive, controllerPassive);
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    frameRenderer.renderInventoryBlock(block, metadata, modelId, renderer);
  }

  public void renderTileEntityAt(TileTcom te) {
    if (te != null) {
      xform.setRotation(te.getFacingDir());
      for (TankSlot tankSlot : TankSlot.values()) {
        tankSlot = notnullJ(tankSlot, "enum.values()[i]");
        renderTrayContents(te, tankSlot, true);
        renderTrayContents(te, tankSlot, false);
      }
    }
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileEntity te = world != null ? world.getTileEntity(x, y, z) : null;
    IFrameworkMachine frameworkMachine = te instanceof IFrameworkMachine ? (IFrameworkMachine) te : null;
    TileTcom tileTcom = te instanceof TileTcom ? (TileTcom) te : null;

    if (frameworkMachine != null && tileTcom != null) {

      xform.setRotation(tileTcom.getFacingDir());
      FaceRenderer.setLightingReference(world, BlockIHopper.blockIHopper, x, y, z);
      Tessellator.instance.addTranslation(x, y, z);

      for (TankSlot tankSlot : TankSlot.values()) {
        tankSlot = notnullJ(tankSlot, "enum.values()[i]");
        renderTray(tankSlot, true);
        renderTray(tankSlot, false);
      }

      Tessellator.instance.addTranslation(-x, -y, -z);
      FaceRenderer.clearLightingReference();
    }

    return frameRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockTcom.blockTcom.getRenderType();
  }

  private void renderTray(@Nonnull TankSlot tankSlot, boolean lower) {

    if ((tankSlot == FRONT_LEFT && !lower) || (tankSlot == FRONT_RIGHT && lower)) {
      return; // controller / tank
    }

    IIcon icon_side = BlockTcom.blockTcom.getIcon(SOUTH.ordinal(), 0);
    IIcon icon_bottom = BlockTcom.blockTcom.getIcon(DOWN.ordinal(), 0);
    IIcon icon_top = BlockTcom.blockTcom.getIcon(UP.ordinal(), 0);

    int[] pos = frameRenderer.translateToSlotPosition(SOUTH, tankSlot);

    IIcon[] icons1 = { null, null, icon_side, icon_side, icon_side, icon_side };

    BoundingBox bb1 = makePartialBBofSlot(0, 0, 0, 16, 16, 16, pos);
    BoundingBox bb2 = makePartialBBofSlot(1, 0, 0, 15, 16, 16, pos);
    BoundingBox bb3 = makePartialBBofSlot(0, 0, 1, 16, 16, 15, pos);
    BoundingBox bb4 = makePartialBBofSlot(0, 0, 0, 16, 7, 16, pos);
    BoundingBox bb5 = bb1.translate(0, 0.05f, 0);

    if (lower) {
      bb1 = bb1.translate(0, -8f / 16f, 0);
      bb2 = bb2.translate(0, -8f / 16f, 0);
      bb3 = bb3.translate(0, -8f / 16f, 0);
      bb4 = bb4.translate(0, -8f / 16f, 0);
      bb5 = bb5.translate(0, -8f / 16f, 0);
    }

    // top box
    renderCube(bb1, icons1, xform, brightnessPerSide, false);
    renderSingleFace(bb4, UP, icon_top, 0, 16, 0, 16, xform, brightnessPerSide, false);

    // inside
    renderSingleFace(bb5, DOWN, icon_bottom, 0, 16, 0, 16, xform, brightnessPerSide, true);
    renderSingleFace(bb2, EAST, icon_side, 0, 16, 0, 16, xform, brightnessPerSide, true);
    renderSingleFace(bb2, WEST, icon_side, 0, 16, 0, 16, xform, brightnessPerSide, true);
    renderSingleFace(bb3, NORTH, icon_side, 0, 16, 0, 16, xform, brightnessPerSide, true);
    renderSingleFace(bb3, SOUTH, icon_side, 0, 16, 0, 16, xform, brightnessPerSide, true);
  }

  private void renderTrayItems(@Nonnull TileTcom te, @Nonnull TankSlot tankSlot) {
    int[] pos = frameRenderer.translateToSlotPosition(SOUTH, tankSlot);

    RenderUtil.bindItemTexture();
    for (Mats mat : new Mats[] { LEATHER, STRING }) {
      IIcon icon = mat == LEATHER ? Items.leather.getIconFromDamage(0) : Items.string.getIconFromDamage(0);
      float rawAmount = te.engine.getAmount(mat) / 100;
      int renderAmount = (int) rawAmount * 8;
      if (rawAmount > 0 && renderAmount < 1) {
        renderAmount = 1;
      } else if (renderAmount > 7) {
        renderAmount = 7;
      }
      for (int i = 0; i < renderAmount; i++) {
        int offsetx = 6 + ((i + mat.ordinal()) & 0b01) * 4;
        int offsetz = 6 + (((i + mat.ordinal()) & 0b10) >> 1) * 4;
        BoundingBox bbi = makePartialBBofSlot(offsetx - 4, i, offsetz - 4, offsetx + 4, 16, offsetz - 4, pos);
        renderSingleFace(bbi, DOWN, icon, 0, 16, 0, 16, xform, brightnessPerSide, false);
        renderSingleFace(bbi, DOWN, icon, 0, 16, 0, 16, xform, brightnessPerSide, true);
      }
    }
  }

  private void renderTrayContents(@Nonnull TileTcom te, @Nonnull TankSlot tankSlot, boolean lower) {
    Block toRender = null;
    Mats mat = null;
    switch (tankSlot) {
    case BACK_LEFT:
      toRender = lower ? Blocks.cobblestone : Blocks.iron_block;
      mat = lower ? COBBLE : IRON;
      break;
    case BACK_RIGHT:
      toRender = lower ? Blocks.gold_block : Blocks.diamond_block;
      mat = lower ? GOLD : DIAMOND;
      break;
    case FRONT_LEFT:
      if (lower) {
        toRender = Blocks.planks;
        mat = WOOD;
      } else {
        return; // controller here
      }
      break;
    case FRONT_RIGHT:
      if (lower) {
        return; // tank here
      } else {
        renderTrayItems(te, tankSlot);
      }
      break;
    }

    int[] pos = frameRenderer.translateToSlotPosition(SOUTH, tankSlot);

    EngineTcom e = te.engine;
    IIcon[] icons = RenderUtil.getBlockTextures(toRender, 0);
    RenderUtil.bindBlockTexture();
    float rawAmount = e.getAmount(mat) / 100;
    if (mat == Mats.WOOD) {
      rawAmount += e.getAmount(STICK) / 100;
    }
    int renderAmount = (int) rawAmount * 16;
    if (rawAmount > 0 && renderAmount < 1) {
      renderAmount = 1;
    } else if (renderAmount > 16) {
      renderAmount = 16;
    }

    BoundingBox bb = makePartialBBofSlot(1, 1, 1, 15, 15, 15, pos);
    if (lower) {
      bb = bb.translate(0, -8f / 16f, 0);
    }
    renderCube(bb, icons, xform, brightnessPerSide, false);
  }

  private static final double px = 1D / 16D;
  private static BoundingBox makePartialBBofSlot(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int[] pos) {
    BoundingBox bb = new BoundingBox(px * minX, px * minY, px * minZ, px * maxX, px * maxY, px * maxZ);
    bb = centerscale(bb, px * 6D, px * 6D, px * 6D);
    bb = bb.translate(pos[0] * 4f / 16f, 4f / 16f, pos[1] * 4f / 16f);
    return bb;
  }

  private static BoundingBox centerscale(BoundingBox bb, double x, double y, double z) {
    return new BoundingBox((bb.minX - 0.5d) * x + 0.5d, (bb.minY - 0.5d) * y + 0.5d, (bb.minZ - 0.5d) * z + 0.5d, (bb.maxX - 0.5d) * x + 0.5d, (bb.maxY - 0.5d)
        * y + 0.5d, (bb.maxZ - 0.5d) * z + 0.5d);
  }

}
