package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IoMode;

public class BlockAfarm extends AbstractMachineBlock<TileAfarm> {

  public static final ModObject ModObject_blockAfarm = EnumHelper.addEnum(ModObject.class, "blockAfarm", new Class<?>[0], new Object[0]);
  public static BlockAfarm blockAfarm;
  public int localRenderId;

  public static BlockAfarm create() {
    blockAfarm = new BlockAfarm();
    blockAfarm.init();
    return blockAfarm;
  }

  protected BlockAfarm() {
    super(ModObject_blockAfarm, TileAfarm.class);
    setBlockBounds(0, 0, 0, 1, 0.85f, 1);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileAfarm) {
      return new ContainerAfarm(player.inventory, (TileAfarm) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileAfarm) {
      return new GuiAfarm(player.inventory, (TileAfarm) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_AFARM;
  }

  @Override
  public int getRenderType() {
    return localRenderId;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":AfarmSide";
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":AfarmSide";
  }

  @Override
  protected String getBackIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":AfarmSide";
  }

  public static IIcon farmlight;
  public static final IIcon[] darkPlantIcons = new IIcon[8];

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    farmlight = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":farmlight");
    darkPlantIcons[0] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant0");
    darkPlantIcons[1] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant0");
    darkPlantIcons[2] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant0");
    darkPlantIcons[3] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant1");
    darkPlantIcons[4] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant2");
    darkPlantIcons[5] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant3");
    darkPlantIcons[6] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant4");
    darkPlantIcons[7] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":darkplant5");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getOverlayIconForMode(TileAfarm tile, ForgeDirection face, IoMode mode) {
    if (face != ForgeDirection.DOWN) {
      return null;
    }
    return super.getOverlayIconForMode(tile, face, mode);
  }

}
