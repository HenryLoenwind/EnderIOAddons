package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

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

}
