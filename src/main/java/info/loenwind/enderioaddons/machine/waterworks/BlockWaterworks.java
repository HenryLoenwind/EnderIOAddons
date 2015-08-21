package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.machine.framework.IFrameworkBlock;
import info.loenwind.enderioaddons.machine.framework.TextureProvider;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockWaterworks extends AbstractMachineBlock<TileWaterworks> implements IFrameworkBlock, TextureProvider {

  public static final ModObject ModObject_blockWaterworks = EnumHelper.addEnum(ModObject.class, "blockWaterworks", new Class<?>[0], new Object[0]);
  public static BlockWaterworks blockWaterworks;
  public static int renderId;

  public static BlockWaterworks create() {
    blockWaterworks = new BlockWaterworks();
    blockWaterworks.init();
    return blockWaterworks;
  }

  protected BlockWaterworks() {
    super(ModObject_blockWaterworks, TileWaterworks.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileWaterworks) {
      return new ContainerWaterworks(player.inventory, (TileWaterworks) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileWaterworks) {
      return new GuiWaterworks(player.inventory, (TileWaterworks) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_WATERWORKS;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:machineTemplate";
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  protected String getModelIconKey(boolean active) {
    return "enderioaddons:frameworkModel";
  }

  @Override
  public String getControllerModelName() {
    return "waterController";
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) { // TODO: steam
    if (isActive(world, x, y, z) && !world.getBlock(x, y + 1, z).isOpaqueCube()) {
      float startX = x + 0.8F - rand.nextFloat() * 0.6F;
      float startY = y + 1.0F;
      float startZ = z + 0.8F - rand.nextFloat() * 0.6F;
      if (rand.nextInt(20) == 0) {
        world.spawnParticle("lava", startX, startY, startZ, 0.0D, -0.2D, 0.0D);
      } else {
        world.spawnParticle("smoke", startX, startY, startZ, 0.0D, 0.0D, 0.0D);
      }
    }
  }

  IIcon controllerTexture;

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);

    controllerTexture = iIconRegister.registerIcon("enderioaddons:frameworkController");
  }

  @Override
  public IIcon getTexture() {
    return controllerTexture;
  }

}
