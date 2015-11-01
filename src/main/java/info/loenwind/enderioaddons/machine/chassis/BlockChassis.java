package info.loenwind.enderioaddons.machine.chassis;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.Config;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;

public class BlockChassis extends BlockEio {

  public static final ModObject ModObject_blockChassis = EnumHelper.addEnum(ModObject.class, "blockChassis", new Class<?>[0], new Object[0]);
  public static BlockChassis blockChassis;

  private IIcon[] blockIcons = new IIcon[17];

  public static BlockChassis create() {
    blockChassis = new BlockChassis();
    blockChassis.init();
    return blockChassis;
  }

  public BlockChassis() {
    super(ModObject_blockChassis.unlocalisedName, null);
    setHardness(1.5F);
    setStepSound(soundTypeMetal);
    setHarvestLevel("pickaxe", 1);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, ItemChassis.class, ModObject_blockChassis.unlocalisedName);
  }

  private static int[] iconMapping1 = { 0, 1, 2, 2, 2, 2 };
  private static int[] iconMapping2 = { 0, 1, 3, 3, 3, 3 };

  @Override
  public IIcon getIcon(int side, int meta) {
    switch (meta) {
    case 5:
      return blockIcons[1 + iconMapping1[side]];
    case 6:
      return blockIcons[1 + iconMapping2[side]];
    case 11:
      return blockIcons[7 + iconMapping1[side]];
    case 12:
      return blockIcons[7 + iconMapping2[side]];
    case 15:
      if (Config.decoBlockInvisibleIsInvisible.getBoolean()) {
        return blockIcons[meta];
      } else {
        return blockIcons[meta + 1];
      }
    default:
      return blockIcons[meta];
    }
  }

  @Override
  public int damageDropped(int meta) {
    return meta;
  }

  @Override
  public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
    for (int i = 0; i < 16; i++) {
      list.add(new ItemStack(this, 1, i));
    }
  }

  @Override
  public boolean renderAsNormalBlock() {
    return true;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcons[0] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":machineChassi");
    blockIcons[1] = iIconRegister.registerIcon("enderio:machineTemplate");
    blockIcons[2] = iIconRegister.registerIcon("enderio:machineTop");
    blockIcons[3] = iIconRegister.registerIcon("enderio:machineSide");
    blockIcons[4] = iIconRegister.registerIcon("enderio:machineBack");
    blockIcons[5] = null; // machine all sides side
    blockIcons[6] = null; // machine all sides back
    blockIcons[7] = iIconRegister.registerIcon("enderio:blockSoulMachineBlank");
    blockIcons[8] = iIconRegister.registerIcon("enderio:blockSoulMachineTop");
    blockIcons[9] = iIconRegister.registerIcon("enderio:blockSoulMachineSide");
    blockIcons[10] = iIconRegister.registerIcon("enderio:blockSoulMachineBack");
    blockIcons[11] = null; // soul machine all sides side
    blockIcons[12] = null; // soul machine all sides back
    blockIcons[13] = iIconRegister.registerIcon("enderio:capacitorBankCreativeBorder");
    blockIcons[14] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":itemBrokenSpawner");
    blockIcons[15] = iIconRegister.registerIcon("enderio:blank");
    blockIcons[16] = iIconRegister.registerIcon("enderio:fusedGlassFrame");
  }

}
