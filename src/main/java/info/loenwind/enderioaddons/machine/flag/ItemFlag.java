package info.loenwind.enderioaddons.machine.flag;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.EnderIOTab;

public class ItemFlag extends ItemBlock implements IAdvancedTooltipProvider {

  public ItemFlag() {
    this(BlockFlag.blockFlag);
  }

  public ItemFlag(Block block) {
    super(block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
    p_150895_3_.add(new ItemStack(p_150895_1_, 1, 0));
    p_150895_3_.add(new ItemStack(p_150895_1_, 1, 1));
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    BlockFlag.blockFlag.addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    BlockFlag.blockFlag.addBasicEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    BlockFlag.blockFlag.addDetailedEntries(itemstack, entityplayer, list, flag);
  }

}
