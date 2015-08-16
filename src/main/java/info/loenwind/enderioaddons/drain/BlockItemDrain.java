package info.loenwind.enderioaddons.drain;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;

public class BlockItemDrain extends ItemBlockWithMetadata implements IAdvancedTooltipProvider {

  public BlockItemDrain() {
    super(BlockDrain.blockDrain, BlockDrain.blockDrain);
    setHasSubtypes(false);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  public BlockItemDrain(Block block) {
    super(block, block);
    setHasSubtypes(false);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
	  BlockDrain.blockDrain.addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
	  BlockDrain.blockDrain.addBasicEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
	  BlockDrain.blockDrain.addDetailedEntries(itemstack, entityplayer, list, flag);
  }



}
