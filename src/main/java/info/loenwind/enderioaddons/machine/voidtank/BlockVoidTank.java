package info.loenwind.enderioaddons.machine.voidtank;

import static info.loenwind.enderioaddons.render.FaceRenderer.sideAndFacingToSpriteOffset;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;

public class BlockVoidTank extends AbstractMachineBlock<TileVoidTank> implements IAdvancedTooltipProvider {

  public static final ModObject ModObject_blockVoidTank = EnumHelper.addEnum(ModObject.class, "blockVoidTank", new Class<?>[0], new Object[0]);
  public static BlockVoidTank blockVoidTank;
  public int localRenderId;

  public static BlockVoidTank create() {
    PacketHandler.INSTANCE.registerMessage(PacketVoidTank.class, PacketVoidTank.class, PacketHandler.nextID(), Side.CLIENT);
    blockVoidTank = new BlockVoidTank();
    blockVoidTank.init();
    return blockVoidTank;
  }

  protected BlockVoidTank() {
    super(ModObject_blockVoidTank, TileVoidTank.class);
    setStepSound(Block.soundTypeGlass);
    setLightOpacity(0);
    setBlockName(name);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemVoidTank.class, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
    list.add(new ItemStack(this, 1, 0));
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileVoidTank();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (!(te instanceof TileVoidTank)) {
      return null;
    }
    return new ContainerVoidTank(player.inventory, (TileVoidTank) te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    final TileEntity te = world.getTileEntity(x, y, z);
    final InventoryPlayer inventory = player.inventory;
    if (te instanceof TileVoidTank && inventory != null) {
      return new GuiVoidTank(inventory, (TileVoidTank) te);
    }
    return null;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_VOIDTANK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    // used to render the block in the world
    TileEntity te = world.getTileEntity(x, y, z);
    int facing = 0;
    if (te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      facing = me.facing;
    }
    return iconBuffer[0][sideAndFacingToSpriteOffset[blockSide][facing]];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    return iconBuffer[0][blockSide];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {

    iconBuffer = new IIcon[2][24];
    // first outer hull, then the other layers
    // DOWN, UP, back, front, WEST, EAST
    iconBuffer[0][0] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_di");
    iconBuffer[0][1] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_ti");
    iconBuffer[0][2] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_ri");
    iconBuffer[0][3] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_li");
    iconBuffer[0][4] = iIconRegister.registerIcon("enderio:blockTankAdvanced");
    iconBuffer[0][5] = iIconRegister.registerIcon("enderio:blank");

    iconBuffer[0][6] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_d1");
    iconBuffer[0][7] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_t1");
    iconBuffer[0][8] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_r1");
    iconBuffer[0][9] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_l1");
    iconBuffer[0][10] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_b1");
    iconBuffer[0][11] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_f1");

    iconBuffer[0][12] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_d2");
    iconBuffer[0][13] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_t2");
    iconBuffer[0][14] = null;
    iconBuffer[0][15] = null;
    iconBuffer[0][16] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_b2");
    iconBuffer[0][17] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_f2");

    iconBuffer[0][18] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_d3");
    iconBuffer[0][19] = null;
    iconBuffer[0][20] = null;
    iconBuffer[0][21] = null;
    iconBuffer[0][22] = iIconRegister.registerIcon(EnderIOAddons.DOMAIN + ":void/void_b3");
    iconBuffer[0][23] = null;

    registerOverlayIcons(iIconRegister);
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if (itemstack.stackTagCompound != null && itemstack.stackTagCompound.hasKey("tankContents")) {
      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) itemstack.stackTagCompound.getTag("tankContents"));
      if (fl != null && fl.getFluid() != null) {
        String str = fl.amount + " " + EnderIO.lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " "
            + fl.getFluid().getLocalizedName(fl);
        list.add(str);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileVoidTank) {
      TileVoidTank tank = (TileVoidTank) te;
      FluidStack stored = tank.tank.getFluid();
      String fluid = stored == null ? EnderIO.lang.localize("tooltip.none") : stored.getFluid().getLocalizedName(stored);
      int amount = stored == null ? 0 : stored.amount;

      tooltip.add(String.format("%s%s : %s (%d %s)", EnumChatFormatting.WHITE, EnderIO.lang.localize("tooltip.fluidStored"), fluid, amount,
          EnderIO.lang.localize("fluid.millibucket.abr")));
    }
  }

  @Override
  public int getRenderType() {
    return localRenderId;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    if (rand.nextInt(200) == 0) {
      world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "portal.portal", 0.25F, rand.nextFloat() * 0.4F + 0.4F, false);
    }

    for (int l = 0; l < 2; ++l) {
      int i1 = rand.nextInt(2) * 2 - 1; // -1 or 1
      double d0 = x + 0.5D + 0.25D * i1;
      double d3 = rand.nextFloat() * 1.0F * i1;
      i1 = rand.nextInt(2) * 2 - 1; // -1 or 1
      double d1 = y + 0.5D + 0.25D * i1;
      double d4 = rand.nextFloat() * 1.0F * i1;
      i1 = rand.nextInt(2) * 2 - 1; // -1 or 1
      double d2 = z + 0.5D + 0.25D * i1;
      double d5 = rand.nextFloat() * 1.0F * i1;

      world.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderBlockPass() {
    return 1;
  }

}
