package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.machine.framework.IFrameworkBlock;
import info.loenwind.enderioaddons.machine.framework.TextureProvider;
import info.loenwind.enderioaddons.machine.part.MachinePart;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
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
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    if (isActive(world, x, y, z)) {
      float startX, startY, startZ;

      startX = x + 0.8F - rand.nextFloat() * 0.6F;
      startY = y + 1.0F;
      startZ = z + 0.8F - rand.nextFloat() * 0.6F;
      if (rand.nextInt(20) == 0) {
        world.spawnParticle("smoke", startX, startY, startZ, 0.0D, 0.0D, 0.0D);
      }
      EntitySmokeFX entityfx = new EntitySmokeFX(world, startX, startY, startZ, 0.0D, 0.0D, 0.0D, 3.5F);
      entityfx.setRBGColorF(1f, 1f, 1f);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);

      startY = y + 0.65F + rand.nextFloat() * 0.15F;
      if (rand.nextBoolean()) {
        startX = rand.nextBoolean() ? x + 1f + rand.nextFloat() * 0.15F : x - rand.nextFloat() * 0.15F;
        startZ = z + 0.8F - rand.nextFloat() * 0.6F;
      } else {
        startX = x + 0.8F - rand.nextFloat() * 0.6F;
        startZ = rand.nextBoolean() ? z + 1f + rand.nextFloat() * 0.15F : z - rand.nextFloat() * 0.15F;
      }
      entityfx = new EntitySmokeFX(world, startX, startY, startZ, 0.0D, 0.0D, 0.0D, 3.5F);
      entityfx.setRBGColorF(1f, 1f, 1f);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
    }
  }

  IIcon controllerTexture;
  IIcon filterTexture;

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);

    controllerTexture = iIconRegister.registerIcon("enderioaddons:frameworkController");
    filterTexture = iIconRegister.registerIcon(MachinePart.FILTER_ELEMENT.iconKey);
  }

  @Override
  public IIcon getTexture() {
    return controllerTexture;
  }

}
