package info.loenwind.enderioaddons.machine.framework;

import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine.TankSlot;
import info.loenwind.enderioaddons.machine.part.MachinePart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.ClientProxy;
import crazypants.enderio.machine.AbstractMachineEntity;

public class RendererFrameworkMachine extends TechneMachineRenderer<AbstractTileFramework> {

  private Map<String, GroupObject> controller = new HashMap<String, GroupObject>();

  private GroupObject[] tanks = { null, null, null, null };
  private GroupObject[] valves = { null, null, null, null };
  private GroupObject[] stems = { null, null };
  private GroupObject[] contr = { null, null, null, null, null, null, null, null, null, null };

  public RendererFrameworkMachine() {
    super(-1, "models/framework");

    for (int i = 1; i <= 4; i++) {
      tanks[i - 1] = model.remove("tank" + i);
      valves[i - 1] = model.remove("Valve" + i);
    }
    for (int i = 1; i <= 2; i++) {
      stems[i - 1] = model.remove("ValveStem" + i);
    }
    for (int i = 1; i <= 10; i++) {
      contr[i - 1] = model.remove("Controller" + i);
    }
  }

  public GroupObject extractModelPart(String name) {
    return model.remove(name);
  }

  public GroupObject getControllerPart(int id) {
    return contr[id - 1];
  }

  public void registerController(String name, GroupObject active, GroupObject inactive) {
    controller.put(name, inactive);
    controller.put(name + "Active", active);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileEntity te = world != null ? world.getTileEntity(x, y, z) : null;
    IFrameworkMachine frameworkMachine = te instanceof IFrameworkMachine ? (IFrameworkMachine) te : null;
    AbstractMachineEntity machineEntity = te instanceof AbstractMachineEntity ? (AbstractMachineEntity) te : null;

    if (frameworkMachine != null && machineEntity != null) {
      float[] brightnessPerSide = new float[6];
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightnessPerSide[dir.ordinal()] = Math.max(RenderUtil.getColorMultiplierForFace(dir) + 0.1f, 1f);
      }

      for (TankSlot tankSlot : TankSlot.values()) {
        if (frameworkMachine.renderSlot(tankSlot)) {
          renderSubBlock(x, y, z, machineEntity, brightnessPerSide, tankSlot);
        }
      }
    }

    return super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
  }

  private static void renderSubBlock(int x, int y, int z, AbstractMachineEntity te, float[] brightnessPerSide, TankSlot tankSlot) {
    BoundingBox bb = BoundingBox.UNIT_CUBE;

    int[] pos = translateToSlotPosition(te.getFacingDir(), tankSlot);
    bb = bb.translate(pos[0] * 4f / 16f, 4f / 16f, pos[1] * 4f / 16f);
    bb = bb.scale(6D / 16D, 6D / 16D, 6D / 16D);
    bb = bb.translate(x, y, z);

    IIcon[] icons = getBlockTextures(te, tankSlot);
    CubeRenderer.render(bb, icons, null, brightnessPerSide);
  }

  private static ForgeDirection turn(ForgeDirection dir, TankSlot tankSlot) {
    if (tankSlot == TankSlot.BACK_RIGHT) {
      return dir.getRotation(ForgeDirection.DOWN);
    } else if (tankSlot == TankSlot.BACK_LEFT) {
      return dir.getOpposite();
    } else if (tankSlot == TankSlot.FRONT_LEFT) {
      return dir.getRotation(ForgeDirection.UP);
    } else {
      return dir;
    }
  }

  private static final int[][] positions = { { -1, 1 }, { 1, 1 }, { 1, -1 }, { -1, -1 } };

  private static int[] translateToSlotPosition(ForgeDirection dir, TankSlot tankSlot) {
    switch (dir) {
    case NORTH:
      return positions[(2 + tankSlot.ordinal()) & 3];
    case SOUTH:
      return positions[(0 + tankSlot.ordinal()) & 3];
    case WEST:
      return positions[(3 + tankSlot.ordinal()) & 3];
    case EAST:
      return positions[(1 + tankSlot.ordinal()) & 3];
    default:
      break;
    }
    return positions[0];
  }

  public static IIcon[] getBlockTextures(AbstractMachineEntity te, TankSlot tankSlot) {
    IFrameworkMachine fm = (IFrameworkMachine) te;
    int facing = turn(te.getFacingDir(), tankSlot).ordinal();
    IIcon[] icons = new IIcon[6];
    int i = 0;
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if (te.isActive()) {
        icons[i++] = fm.getSlotIcon(tankSlot, ClientProxy.sideAndFacingToSpriteOffset[dir.ordinal()][facing] + 6);
      } else {
        icons[i++] = fm.getSlotIcon(tankSlot, ClientProxy.sideAndFacingToSpriteOffset[dir.ordinal()][facing]);
      }
    }
    return icons;
  }

  @Override
  protected Collection<GroupObject> getModel(Block block, int metadata) {
    List<GroupObject> result = new ArrayList<GroupObject>();
    if (renderFrameInItem(block, metadata)) {
      result.addAll(super.getModel(block, metadata));
    }
    String controllerName = getControllerInItemName(block, metadata);
    if (controllerName != null) {
      result.add(controller.get(controllerName + "Active"));
    }
    if (renderAllTanksInItem(block, metadata)) {
      for (GroupObject groupObject : tanks) {
        result.add(groupObject);
      }
    } else if (renderOneTankInItem(block, metadata)) {
      result.add(tanks[TankSlot.FRONT_LEFT.ordinal()]);
    }
    return result;
  }

  @Override
  protected Collection<GroupObject> getModel(IBlockAccess world, int x, int y, int z) {
    List<GroupObject> result = new ArrayList<GroupObject>(super.getModel(world, x, y, z));

    TileEntity te = world != null ? world.getTileEntity(x, y, z) : null;
    IFrameworkMachine frameworkMachine = te instanceof IFrameworkMachine ? (IFrameworkMachine) te : null;
    AbstractMachineEntity machineEntity = te instanceof AbstractMachineEntity ? (AbstractMachineEntity) te : null;

    if (frameworkMachine != null && machineEntity != null) {

      if (frameworkMachine.hasController()) {
        if (machineEntity.isActive()) {
          result.add(controller.get(frameworkMachine.getControllerModelName() + "Active"));
        } else {
          result.add(controller.get(frameworkMachine.getControllerModelName()));
        }
      }

      boolean stem1 = false, stem2 = false;
      for (TankSlot tankSlot : TankSlot.values()) {
        if (frameworkMachine.hasTank(tankSlot)) {
          int i = tankSlot.ordinal();
          result.add(tanks[i]);
          result.add(valves[i]);
          switch (tankSlot) {
          case FRONT_LEFT:
          case BACK_LEFT:
            stem1 = true;
            break;
          case FRONT_RIGHT:
          case BACK_RIGHT:
            stem2 = true;
            break;
          }
        }
      }
      if (stem1) {
        result.add(stems[0]);
      }
      if (stem2) {
        result.add(stems[1]);
      }
    }

    return result;
  }

  @SuppressWarnings("static-method")
  protected boolean renderFrameInItem(@SuppressWarnings("unused") Block block, int metadata) {
    if ((metadata & 16) == 16) {
      return MachinePart.values()[metadata & 15].hasFrame;
    }
    return true;
  }

  @SuppressWarnings("static-method")
  protected String getControllerInItemName(Block block, int metadata) {
    if ((metadata & 16) == 16) {
      return MachinePart.values()[metadata & 15].getControllerModelName();
    } else if (block instanceof IFrameworkBlock) {
      return ((IFrameworkBlock) block).getControllerModelName();
    }
    return null;
  }

  @SuppressWarnings("static-method")
  protected boolean renderAllTanksInItem(@SuppressWarnings("unused") Block block, int metadata) {
    if ((metadata & 16) == 16) {
      return MachinePart.values()[metadata & 15].hasTanks;
    }
    return true;
  }

  @SuppressWarnings("static-method")
  protected boolean renderOneTankInItem(@SuppressWarnings("unused") Block block, int metadata) {
    if ((metadata & 16) == 16) {
      return MachinePart.values()[metadata & 15].hasSingleTank;
    }
    return false;
  }

}
