package info.loenwind.enderioaddons.machine.drain;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.EnderIOTab;

public class BlockItemDrain extends ItemBlockWithMetadata implements IAdvancedTooltipProvider {

  public BlockItemDrain() {
    this(BlockDrain.blockDrain);
  }

  public BlockItemDrain(Block block) {
    super(block, block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    String result = super.getUnlocalizedName(par1ItemStack);
    if (meta == 1) {
      result += ".food";
    }
    return result;
  }

  @Override
  public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
    p_150895_3_.add(new ItemStack(p_150895_1_, 1, 0));
    p_150895_3_.add(new ItemStack(p_150895_1_, 1, 1));
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
