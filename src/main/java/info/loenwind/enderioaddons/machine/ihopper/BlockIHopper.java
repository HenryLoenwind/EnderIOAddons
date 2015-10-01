package info.loenwind.enderioaddons.machine.ihopper;

import static info.loenwind.enderioaddons.common.NullHelper.notnull;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.machine.framework.IFrameworkBlock;
import info.loenwind.enderioaddons.machine.framework.ITextureProvider;

import java.util.Random;

import javax.annotation.Nonnull;

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
import crazypants.enderio.network.PacketHandler;

public class BlockIHopper extends AbstractMachineBlock<TileIHopper> implements IFrameworkBlock, ITextureProvider {

  public static final ModObject ModObject_blockIHopper = EnumHelper.addEnum(ModObject.class, "blockIHopper", new Class<?>[0], new Object[0]);
  public static BlockIHopper blockIHopper;
  public int localRenderId;

  public static BlockIHopper create() {
    PacketHandler.INSTANCE.registerMessage(PacketIHopper.class, PacketIHopper.class, PacketHandler.nextID(), Side.SERVER);
    blockIHopper = new BlockIHopper();
    blockIHopper.init();
    return blockIHopper;
  }

  protected BlockIHopper() {
    super(ModObject_blockIHopper, TileIHopper.class);
  }

  @Nonnull
  public static BlockIHopper getBlock() {
    return notnull(blockIHopper, "BlockIHopper has not been initialized");
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileIHopper) {
      return new ContainerIHopper(player.inventory, (TileIHopper) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileIHopper) {
      return new GuiIHopper(player.inventory, (TileIHopper) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_IHOPPER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:machineTemplate";
  }

  @Override
  public int getRenderType() {
    return localRenderId;
  }

  @Override
  protected String getModelIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":frameworkModel";
  }

  @Override
  public @Nonnull String getControllerModelName() {
    return "ihopperController";
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
    if (isActive(world, x, y, z) && !world.getBlock(x, y + 1, z).isOpaqueCube()) {
      if (rand.nextInt(8) == 0) {
        float startX = x + 0.8F - rand.nextFloat() * 0.6F;
        float startY = y + 1.0F;
        float startZ = z + 0.8F - rand.nextFloat() * 0.6F;
        world.spawnParticle("reddust", startX, startY, startZ, 0.0D, -0.2D, 0.0D);
      }
    }
  }

  IIcon controllerTexture;

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);

    controllerTexture = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":frameworkController");
  }

  @Override
  public IIcon getTexture() {
    return controllerTexture;
  }

}
