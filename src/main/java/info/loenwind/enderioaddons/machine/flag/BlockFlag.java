package info.loenwind.enderioaddons.machine.flag;

import static info.loenwind.enderioaddons.config.Config.flagDemagnetizingChance;
import static info.loenwind.enderioaddons.config.Config.flagKeepTargetOnBreaking;
import static info.loenwind.enderioaddons.network.PacketParticles.spawnParticle;
import info.loenwind.enderioaddons.EnderIOAddons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityEnder;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import crazypants.enderio.waila.IWailaInfoProvider;

public class BlockFlag extends BlockEio implements IAdvancedTooltipProvider, IWailaInfoProvider {

  public static final ModObject ModObject_blockFlag = EnumHelper.addEnum(ModObject.class, "blockFlag", new Class<?>[0], new Object[0]);
  public static BlockFlag blockFlag;
  private static final IIcon[] icons = new IIcon[12];

  /**
   * obeliskRenderer ignores meta, this allows us to draw items with meta in
   * certain places without copying the whole renderer
   */
  public static boolean fakeMeta = false;

  public static BlockFlag create() {
    blockFlag = new BlockFlag();
    blockFlag.init();
    return blockFlag;
  }

  protected BlockFlag() {
    super(ModObject_blockFlag.unlocalisedName, TileFlag.class);
    setHardness(0.5F);
    setStepSound(soundTypeMetal);
    setHarvestLevel("pickaxe", 0);
    setBlockBounds(0.11f, 0, 0.11f, 0.91f, 0.475f, 0.91f);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, ItemFlag.class, ModObject_blockFlag.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, ModObject_blockFlag.unlocalisedName + "TileEntity");
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
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileFlag) {
      if (((TileFlag) te).getParent() != null) {
        return 6;
      }
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    icons[0] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":blockFlagBaseBottom");
    icons[1] = iIconRegister.registerIcon("enderio:machineTop");
    icons[2] = icons[3] = icons[4] = icons[5] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":blockFlagBaseSide");
    icons[6] = icons[7] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":blockFlagMast");
    icons[8] = icons[9] = icons[10] = icons[11] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":blockFlagBaseSideOffline");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    if ((meta != 0 || fakeMeta) && side >= 2 && side <= 5) {
      return icons[side + 6];
    }
    return icons[side];
  }

  @Override
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
    if (side >= 2 && side <= 5) {
      TileEntity te = world.getTileEntity(x, y, z);
      if (te instanceof TileFlag) {
        if (((TileFlag) te).getParent() == null) {
          return super.getIcon(world, x, y, z, side + 6);
        }
      }
    }
    return super.getIcon(world, x, y, z, side);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemstack) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (itemstack != null && itemstack.getItem() instanceof ItemFlag && te instanceof TileFlag) {
      if (itemstack.getItemDamage() != 0) {
        ((TileFlag) te).setCharged(false);
        spawnParticle(world, "reddust", x + .5, y + .5, z + .5, 0, 0, 0);
      } else {
        ((TileFlag) te).readItemStackNBT(itemstack);
        ((TileFlag) te).reparentItemstack(itemstack);
      }
    }
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
    ItemStack itemstack = player.getCurrentEquippedItem();

    if (itemstack != null && itemstack.getItem() instanceof ItemFlag) {
      if (itemstack.getItemDamage() != 0) {
        spawnParticle(world, "reddust", x + par7, y + par8, z + par9, 0, 0, 0);
      } else {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileFlag && ((TileFlag) te).isCharged()) {
          ((TileFlag) te).reparentItemstack(itemstack);
          spawnParticle(world, "happyVillager", x + par7, y + par8, z + par9, 0, 0, 0);
        } else {
          spawnParticle(world, "reddust", x + par7, y + par8, z + par9, 0, 0, 0);
        }
      }
      return true;
    }

    return super.onBlockActivated(world, x, y, z, player, side, par7, par8, par9);
  }

  @Override
  public boolean doNormalDrops(World world, int x, int y, int z) {
    return false;
  }

  @Override
  protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack itemstack) {
    if (te instanceof TileFlag) {
      if (te.getWorldObj().rand.nextDouble() < flagDemagnetizingChance.getDouble()) {
        ((TileFlag) te).setCharged(false);
        itemstack.setItemDamage(1);
        ((TileFlag) te).writeItemStackNBT(itemstack);
        spawnParticle(world, "reddust", x, y, z, 0, 0, 0);
      } else if (!((TileFlag) te).isCharged()) {
        itemstack.setItemDamage(1);
        ((TileFlag) te).writeItemStackNBT(itemstack);
      } else if (flagKeepTargetOnBreaking.getBoolean()) {
        ((TileFlag) te).writeItemStackNBT(itemstack);
      }
    }
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    if (itemstack != null && itemstack.getItem() instanceof ItemFlag) {
      TileFlag te = new TileFlag();
      te.readItemStackNBT(itemstack);
      BlockCoord bc = te.getParent();
      if (bc != null) {
        list.add(EnderIOAddons.lang.localize("flag.tooltip.parent", bc.x, bc.y, bc.z));
      } else if (!te.isCharged() | itemstack.getItemDamage() != 0) {
        list.add(EnderIOAddons.lang.localize("flag.tooltip.uncharged"));
      }
    }
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public void getWailaInfo(List<String> list, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileFlag) {
      BlockCoord bc = ((TileFlag) te).getParent();
      if (bc != null) {
        list.add(EnderIOAddons.lang.localize("flag.tooltip.parent", bc.x, bc.y, bc.z));
      } else if (!((TileFlag) te).isCharged()) {
        list.add(EnderIOAddons.lang.localize("flag.tooltip.uncharged"));
      } else {
        list.add(EnderIOAddons.lang.localize("flag.tooltip.parent.none"));
      }
    }
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return IWailaInfoProvider.ALL_BITS;
  }

  @Override
  public int getRenderType() {
    return BlockObeliskAbstract.defaultObeliskRenderId;
  }

}
