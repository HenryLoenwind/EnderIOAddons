package info.loenwind.enderioaddons.machine.pmon;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.GuiIds;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockPMon extends AbstractMachineBlock<TilePMon> implements IAdvancedTooltipProvider {

  public static final ModObject ModObject_blockPMon = EnumHelper.addEnum(ModObject.class, "blockPMon", new Class<?>[0], new Object[0]);
  public static BlockPMon blockPMon;
  public int localRenderId;

  public static BlockPMon create() {
    PacketHandler.INSTANCE.registerMessage(PacketPMon.ClientHandler.class, PacketPMon.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketPMon.ServerHandler.class, PacketPMon.class, PacketHandler.nextID(), Side.SERVER);
    blockPMon = new BlockPMon();
    blockPMon.init();
    return blockPMon;
  }

  protected BlockPMon() {
    super(ModObject_blockPMon, TilePMon.class);
    setBlockName(name);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, modObject.unlocalisedName);
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
    return new TilePMon();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (!(te instanceof TilePMon)) {
      return null;
    }
    return new ContainerPMon(player.inventory, (TilePMon) te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    final TileEntity te = world.getTileEntity(x, y, z);
    final InventoryPlayer inventory = player.inventory;
    if (te instanceof TilePMon && inventory != null) {
      return new GuiPMon(inventory, (TilePMon) te);
    }
    return null;
  }

  @Override
  public boolean isOpaqueCube() {
    return true;
  }

  @Override
  protected int getGuiId() {
    return GuiIds.GUI_ID_PMON;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return EnderIOAddons.DOMAIN + ":blockPMon";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public int getRenderType() {
    return localRenderId;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
  }
}
