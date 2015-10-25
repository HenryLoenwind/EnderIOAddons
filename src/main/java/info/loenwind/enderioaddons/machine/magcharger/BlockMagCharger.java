package info.loenwind.enderioaddons.machine.magcharger;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;

public class BlockMagCharger extends AbstractMachineBlock<TileMagCharger> {

  public static final ModObject ModObject_blockMagCharger = EnumHelper.addEnum(ModObject.class, "blockMagCharger", new Class<?>[0], new Object[0]);
  public static BlockMagCharger blockMagCharger;
  public int localRenderId;

  public static BlockMagCharger create() {
    blockMagCharger = new BlockMagCharger();
    blockMagCharger.init();
    return blockMagCharger;
  }

  protected BlockMagCharger() {
    super(ModObject_blockMagCharger, TileMagCharger.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileMagCharger) {
      return new ContainerMagCharger(player.inventory, (TileMagCharger) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileMagCharger) {
      return new GuiMagCharger(player.inventory, (TileMagCharger) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_MAGCHARGER;
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
    return EnderIOAddons.DOMAIN + ":chargerSide";
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":chargerSide";
  }

  @Override
  protected String getBackIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":chargerSide";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {

    iconBuffer = new IIcon[2][12];

    // the 6 sides in OFF state
    iconBuffer[0][0] = iIconRegister.registerIcon(getBottomIconKey(false));
    iconBuffer[0][1] = iIconRegister.registerIcon(getTopIconKey(false));
    iconBuffer[0][2] = iIconRegister.registerIcon(getBackIconKey(false));
    iconBuffer[0][3] = iIconRegister.registerIcon(getMachineFrontIconKey(false));
    iconBuffer[0][4] = iIconRegister.registerIcon(getSideIconKey(false));
    iconBuffer[0][5] = iIconRegister.registerIcon(getSideIconKey(false));

    // the extra parts in OFF state
    iconBuffer[0][6] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":chargerBottom");
    iconBuffer[0][7] = iIconRegister.registerIcon(getTopIconKey(false));
    iconBuffer[0][8] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":chargerBar");
    iconBuffer[0][9] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":tcomTrayTop");

    // the 6 sides in ON state
    iconBuffer[1][0] = iIconRegister.registerIcon(getBottomIconKey(true));
    iconBuffer[1][1] = iIconRegister.registerIcon(getTopIconKey(true));
    iconBuffer[1][2] = iIconRegister.registerIcon(getBackIconKey(true));
    iconBuffer[1][3] = iIconRegister.registerIcon(getMachineFrontIconKey(true));
    iconBuffer[1][4] = iIconRegister.registerIcon(getSideIconKey(true));
    iconBuffer[1][5] = iIconRegister.registerIcon(getSideIconKey(true));

    // the extra parts in OFF state
    iconBuffer[1][6] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":chargerBottomActive");
    iconBuffer[1][7] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":chargerTop");
    iconBuffer[1][8] = new IconFlipped(iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":chargerBar"), true, false);
    iconBuffer[1][9] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":tcomTrayTop");

    registerOverlayIcons(iIconRegister);

  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {

    // used to render the block in the world
    if (blockSide <= 5) {
      TileEntity te = world.getTileEntity(x, y, z);
      int facing = 3;
      if (te instanceof AbstractMachineEntity) {
        AbstractMachineEntity me = (AbstractMachineEntity) te;
        facing = me.facing;
      }
      return iconBuffer[isActive(world, x, y, z) ? 1 : 0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
    } else {
      return iconBuffer[isActive(world, x, y, z) ? 1 : 0][blockSide];
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    // This is used to render the block as an item
    return iconBuffer[0][blockSide];
  }

}
