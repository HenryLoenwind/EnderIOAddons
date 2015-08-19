package info.loenwind.enderioaddons.machine.framework;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class AbstractBlockFramework extends AbstractMachineBlock<AbstractTileFramework> {

  public static final ModObject ModObject_blockDummy = EnumHelper.addEnum(ModObject.class, "blockDummy", new Class<?>[0], new Object[0]);
  public static AbstractBlockFramework blockDummy;

  protected AbstractBlockFramework() {
    super(ModObject_blockDummy, null);
    setCreativeTab(null);
  }

  public static AbstractBlockFramework create() {
    blockDummy = new AbstractBlockFramework();
    blockDummy.init();
    return blockDummy;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  protected int getGuiId() {
    return 0;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:machineTemplate";
  }

  @Override
  protected String getModelIconKey(boolean active) {
    return "enderioaddons:frameworkModel";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list) {
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    world.setBlockToAir(x, y, z);
    if (!world.isRemote) {
      world.markBlockForUpdate(x, y, z);
    }
  }
}
