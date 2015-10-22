package info.loenwind.enderioaddons.machine.tcom;

import static info.loenwind.enderioaddons.common.NullHelper.notnullJ;
import static info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot.FRONT_LEFT;
import static info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot.FRONT_RIGHT;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.LEATHER;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STICK;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.STRING;
import static info.loenwind.enderioaddons.machine.tcom.engine.Mats.WOOD;
import static info.loenwind.enderioaddons.render.FaceRenderer.renderCube;
import static info.loenwind.enderioaddons.render.FaceRenderer.renderSingleFace;
import static info.loenwind.enderioaddons.render.FaceRenderer.setupVertices;
import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.machine.framework.GroupObjectWithIcon;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.tcom.engine.EngineTcom;
import info.loenwind.enderioaddons.machine.tcom.engine.Mats;
import info.loenwind.enderioaddons.recipe.Recipes;
import info.loenwind.enderioaddons.render.FaceRenderer;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import crazypants.enderio.EnderIO;

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

      EngineTcom e = te.engine;
      Map<ItemStack, Float> materials = e.getMaterials();
      List<ItemStack> list = GuiTcom.sortMaterialsList(materials);
      int idx = 0;
      leather_or_string_has_rendered = false;
      wood_or_sticks_has_rendered = false;
      for (int lower = 0; lower <= 1; lower++) {
        for (TankSlot tankSlot : TankSlot.values()) {
          tankSlot = notnullJ(tankSlot, "enum.values()[i]");
          if (tankSlot == FRONT_RIGHT && lower == 1) {
            renderEnchantments(te, tankSlot, lower == 1);
          } else if (!(tankSlot == FRONT_LEFT && lower == 0)) {
            while (idx < list.size()) {
              if (renderTrayContents(te, tankSlot, lower == 1, Mats.getMat(list.get(idx++)))) {
                break;
              }
            }
          }
        }
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
    BoundingBox bb5 = makePartialBBofSlot(0, 1, 0, 16, 16, 16, pos);

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

  private boolean renderTrayItems(@Nonnull TileTcom te, @Nonnull TankSlot tankSlot, boolean lower) {
    if (te.engine.getAmount(LEATHER) < 0.01f && te.engine.getAmount(STRING) < 0.01f) {
      return false;
    }

    int[] pos = frameRenderer.translateToSlotPosition(SOUTH, tankSlot);

    Tessellator.instance.draw();
    Tessellator.instance.startDrawingQuads();

    RenderUtil.bindItemTexture();
    for (Mats mat : new Mats[] { LEATHER, STRING }) {
      IIcon icon = mat == LEATHER ? Items.leather.getIconFromDamage(0) : Items.string.getIconFromDamage(0);
      float rawAmount = te.engine.getAmount(mat) / 100;
      int renderAmount = (int) (rawAmount * 8);
      if (rawAmount > 0 && renderAmount < 1) {
        renderAmount = 1;
      } else if (renderAmount > 7) {
        renderAmount = 7;
      }
      for (int i = 0; i < renderAmount; i++) {
        int offsetx = 6 + ((i + mat.ordinal()) & 0b01) * 4;
        int offsetz = 6 + (((i + mat.ordinal()) & 0b10) >> 1) * 4;
        BoundingBox bbi = makePartialBBofSlot(offsetx - 4, 0, offsetz - 4, offsetx + 4, i + 1, offsetz + 4, pos).translate(0,
            0.002f * (mat == LEATHER ? 1 : 2), 0);
        if (lower) {
          bbi = bbi.translate(0, -8f / 16f, 0);
        }
        renderSingleFace(bbi, UP, icon, 0, 16, 0, 16, xform, brightnessPerSide, false);
      }
    }

    Tessellator.instance.draw();
    Tessellator.instance.startDrawingQuads();
    RenderUtil.bindBlockTexture();
    return true;
  }

  private boolean leather_or_string_has_rendered = false;
  private boolean wood_or_sticks_has_rendered = false;

  private boolean renderTrayContents(@Nonnull TileTcom te, @Nonnull TankSlot tankSlot, boolean lower, Mats mat) {
    Block toRender = null;
    switch (mat) {
    case COBBLE:
      toRender = Blocks.cobblestone;
      break;
    case DARKSTEEL:
      toRender = Block.getBlockFromItem(Recipes.darkSteelBlock.getItem());
      break;
    case DIAMOND:
      toRender = Blocks.diamond_block;
      break;
    case GOLD:
      toRender = Blocks.gold_block;
      break;
    case IRON:
      toRender = Blocks.iron_block;
      break;
    case WOOD:
    case STICK:
      if (wood_or_sticks_has_rendered) {
        return false;
      }
      toRender = Blocks.planks;
      wood_or_sticks_has_rendered = true;
      break;
    case LEATHER:
    case STRING:
      if (leather_or_string_has_rendered) {
        return false;
      }
      leather_or_string_has_rendered = true;
      return renderTrayItems(te, tankSlot, lower);
    }

    int[] pos = frameRenderer.translateToSlotPosition(SOUTH, tankSlot);

    EngineTcom e = te.engine;
    IIcon[] icons = RenderUtil.getBlockTextures(toRender, mat.getItemStack().getItemDamage());
    float rawAmount = e.getAmount(mat) / 100;
    if (mat == WOOD) {
      rawAmount = rawAmount + e.getAmount(STICK) / 100 / 2;
    } else if (mat == STICK) {
      rawAmount = e.getAmount(WOOD) / 100 + rawAmount / 2;
    }
    int renderAmount = (int) (rawAmount * 14);
    if (rawAmount > 0 && renderAmount < 1) {
      renderAmount = 1;
    } else if (renderAmount > 14) {
      renderAmount = 14;
    }

    if (renderAmount > 0) {
      BoundingBox bb = makePartialBBofSlot(1, 1, 1, 15, 1 + renderAmount, 15, pos);
      if (lower) {
        bb = bb.translate(0, -8f / 16f, 0);
      }
      FaceRenderer.renderSkirt(bb, icons, 0, 16, 0, 2 + renderAmount, xform, brightnessPerSide, false);
      FaceRenderer.renderSingleFace(bb, UP, icons, 0, 16, 0, 16, xform, brightnessPerSide, false);
      return true;
    } else {
      return false;
    }
  }

  private void renderEnchantments(@Nonnull TileTcom te, @Nonnull TankSlot tankSlot, boolean lower) {
    int[] pos = frameRenderer.translateToSlotPosition(SOUTH, tankSlot);

    EngineTcom e = te.engine;
    float rawAmount = e.getEnchantmentAmounts() / 100;
    int renderAmount = (int) (rawAmount * 16);
    if (rawAmount > 0 && renderAmount < 1) {
      renderAmount = 1;
    } else if (renderAmount > 16) {
      renderAmount = 16;
    }

    if (renderAmount > 0) {
      Tessellator.instance.draw();
      Tessellator.instance.startDrawingQuads();
      RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/blocks/enchantments.png");

      int renderAmount2 = renderAmount / 2;
      if (renderAmount == 0) {
        renderAmount = 1;
      }
      BoundingBox bb = makePartialBBofSlot(0, 0, 0, 16, renderAmount2, 16, pos).scale(.99, 1, .99);
      BoundingBox bb1 = makePartialBBofSlot(1, 0, 1, 15, renderAmount, 15, pos);
      if (lower) {
        bb = bb.translate(0, -8f / 16f, 0);
        bb1 = bb1.translate(0, -8f / 16f, 0);
      }

      if (te.lastRenderTick != EnderIO.proxy.getTickCount()) {
        te.lastRenderTick = EnderIO.proxy.getTickCount();
        te.renderData[0] += te.renderData[1];
        if (te.renderData[0] + 32 >= 320) {
          te.renderData[0]--;
          te.renderData[1] = -1;
        } else if (te.renderData[0] <= -1) {
          te.renderData[0]++;
          te.renderData[1] = 1;
        }
        te.renderData[2] += te.getWorldObj().rand.nextInt(5) - 2;
        if (te.renderData[2] + 32 >= 64) {
          te.renderData[2] = 31;
        } else if (te.renderData[2] < 0) {
          te.renderData[2] = 0;
        }
        te.renderData[3] += te.renderData[1];
        if (te.renderData[3] < 0) {
          te.renderData[3] += 24;
        } else if (te.renderData[3] >= 24) {
          te.renderData[3] -= 24;
        }
      }

      float minU = te.renderData[2] / 64f;
      float maxU = (te.renderData[2] + 32) / 64f;
      float minV = te.renderData[0] / 320f;
      float maxV = (te.renderData[0] + renderAmount * 2) / 320f;
      if (te.renderData[1] < 0) {
        float tmp = minV;
        minV = maxV;
        maxV = tmp;
      }

      setupVertices(bb1, xform);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.WEST, maxU, minU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.EAST, maxU, minU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.UP, 0f, 32f / 64f, 0f, 32f / 320f, xform, brightnessPerSide, false);

      if (te.renderData[1] < 0) {
        minV = (te.renderData[0] + renderAmount2 * 2) / 320f;
      } else {
        maxV = (te.renderData[0] + renderAmount2 * 2) / 320f;
      }

      setupVertices(bb, xform);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.WEST, maxU, minU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.EAST, maxU, minU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.UP, 0f, 32f / 64f, 0f, 32f / 320f, xform, brightnessPerSide, false);
    }

    if (renderAmount < 16) {
      Tessellator.instance.draw();
      Tessellator.instance.startDrawingQuads();
      RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/blocks/enchantmentbase.png");

      BoundingBox bb2 = makePartialBBofSlot(2, 0, 2, 14, 16, 14, pos);
      BoundingBox bb3 = makePartialBBofSlot(3, 0, 3, 13, 16, 13, pos);
      if (lower) {
        bb2 = bb2.translate(0, -8f / 16f, 0);
        bb3 = bb3.translate(0, -8f / 16f, 0);
      }

      float minU = te.renderData[3] / 48f;
      float maxU = minU + 24f / 48f;
      float minV = .5f;
      float maxV = 1f;

      setupVertices(bb2, xform);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, true);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerSide, true);
      renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.WEST, minU, maxU, minV, maxV, xform, brightnessPerSide, true);
      renderSingleFace(ForgeDirection.WEST, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.EAST, minU, maxU, minV, maxV, xform, brightnessPerSide, true);
      renderSingleFace(ForgeDirection.EAST, minU, maxU, minV, maxV, xform, brightnessPerSide, false);

      minU = 0f;
      maxU = 20f / 48f;
      minV = 0f;
      maxV = .5f;

      setupVertices(bb3, xform);
      renderSingleFace(ForgeDirection.SOUTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.NORTH, minU, maxU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.WEST, maxU, minU, minV, maxV, xform, brightnessPerSide, false);
      renderSingleFace(ForgeDirection.EAST, maxU, minU, minV, maxV, xform, brightnessPerSide, false);
    }

    Tessellator.instance.draw();
    Tessellator.instance.startDrawingQuads();
    RenderUtil.bindBlockTexture();
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
