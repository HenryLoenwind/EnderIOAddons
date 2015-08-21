package info.loenwind.enderioaddons.machine.framework;

import info.loenwind.enderioaddons.EnderIOAddons;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.CustomCubeRenderer;
import com.enderio.core.client.render.TechneModelRenderer;
import com.enderio.core.client.render.TechneUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.client.render.VertexTransformComposite;
import com.google.common.collect.ObjectArrays;

import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.OverlayRenderer;

public class TechneMachineRenderer<T extends AbstractMachineEntity> extends TechneModelRenderer {

  private CustomCubeRenderer ccr = new CustomCubeRenderer();
  private OverlayRenderer overlay = new OverlayRenderer();

  public TechneMachineRenderer(AbstractMachineBlock<T> block, String modelPath) {
    super(EnderIOAddons.MODID, modelPath, block.getRenderType(), new VertexTransformComposite(new VertexRotationFacing(ForgeDirection.NORTH)));
  }

  public TechneMachineRenderer(int renderId, String modelPath) {
    super(TechneUtil.getModel(EnderIOAddons.MODID, modelPath), renderId, new VertexTransformComposite(new VertexRotationFacing(ForgeDirection.NORTH)));
  }

  public TechneMachineRenderer<T> addTransform(VertexTransform vt) {
    this.vt = new VertexTransformComposite(ObjectArrays.concat(vt, ((VertexTransformComposite) this.vt).xforms));
    return this;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    setFacingDir(ForgeDirection.SOUTH);
    TechneUtil.vt = this.vt;
    IIcon icon = getModelIcon(block, metadata);
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.setColorOpaque_F(1, 1, 1);
    Tessellator.instance.addTranslation(0, -0.47f, 0);
    for (GroupObject go : getModel(block, metadata)) {
      if (go instanceof GroupObjectWithIcon) {
        TechneUtil.renderWithIcon(go, ((GroupObjectWithIcon) go).getControllerTexture(), renderer.overrideBlockTexture, Tessellator.instance, null, 0, 0,
            0, vt, true);
      } else {
        TechneUtil.renderWithIcon(go, icon, renderer.overrideBlockTexture, Tessellator.instance, null, 0, 0, 0, vt, true);
      }
    }
    Tessellator.instance.addTranslation(0, 0.47f, 0);
    Tessellator.instance.draw();
    TechneUtil.vt = null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    T te = (T) world.getTileEntity(x, y, z);
    if (te != null) {
      setFacingDir(te.getFacingDir());
      overlay.setTile(te);
    }

    TechneUtil.vt = this.vt;
    IIcon icon = getModelIcon(world, x, y, z, block);
    Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
    Tessellator.instance.setColorOpaque_F(1, 1, 1);
    Tessellator.instance.addTranslation(x + .5F, y + 0.0375f, z + .5F);
    for (GroupObject go : getModel(world, x, y, z)) {
      if (go instanceof GroupObjectWithIcon) {
        TechneUtil.renderWithIcon(go, ((GroupObjectWithIcon) go).getControllerTexture(), renderer.overrideBlockTexture, Tessellator.instance, world, x, y,
            z, vt, true);
      } else {
        TechneUtil.renderWithIcon(go, icon, renderer.overrideBlockTexture, Tessellator.instance, world, x, y, z, vt, true);
      }
    }
    Tessellator.instance.addTranslation(-x - .5F, -y - 0.0375f, -z - .5F);
    TechneUtil.vt = null;

    if (renderer.overrideBlockTexture == null) {
      ccr.renderBlock(world, block, x, y, z, overlay);
    }

    return true;
  }

  @Override
  protected IIcon getModelIcon(Block block, int metadata) {
    return ((AbstractMachineBlock<?>) block).getModelIcon();
  }

  @Override
  protected IIcon getModelIcon(IBlockAccess world, int x, int y, int z, Block block) {
    return getModelIcon(block, 0);
  }

  private void setFacingDir(ForgeDirection dir) {
    VertexRotationFacing rot = (VertexRotationFacing) ((VertexTransformComposite) vt).xforms[0];
    rot.setRotation(dir);
  }
}
