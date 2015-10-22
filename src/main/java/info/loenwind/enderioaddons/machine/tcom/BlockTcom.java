package info.loenwind.enderioaddons.machine.tcom;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;
import info.loenwind.enderioaddons.machine.framework.IFrameworkBlock;
import info.loenwind.enderioaddons.machine.framework.ITextureProvider;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockTcom extends AbstractMachineBlock<TileTcom> implements IFrameworkBlock, ITextureProvider {

  public static final ModObject ModObject_blockTcom = EnumHelper.addEnum(ModObject.class, "blockTcom", new Class<?>[0], new Object[0]);
  public static BlockTcom blockTcom;
  public int localRenderId;

  public static BlockTcom create() {
    PacketHandler.INSTANCE.registerMessage(PacketTcomAction.class, PacketTcomAction.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketTcomUpdate.class, PacketTcomUpdate.class, PacketHandler.nextID(), Side.CLIENT);
    blockTcom = new BlockTcom();
    blockTcom.init();
    return blockTcom;
  }

  protected BlockTcom() {
    super(ModObject_blockTcom, TileTcom.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileTcom) {
      return new ContainerTcom(player.inventory, (TileTcom) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileTcom) {
      return new GuiTcom(player.inventory, (TileTcom) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_TCOM;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":tcomTraySide";
  }

  @Override
  protected String getTopIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":tcomTrayTop";
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

  private IIcon controllerTexture;

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

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    Blocks.enchanting_table.randomDisplayTick(world, x, y, z, rand);
  }

}
