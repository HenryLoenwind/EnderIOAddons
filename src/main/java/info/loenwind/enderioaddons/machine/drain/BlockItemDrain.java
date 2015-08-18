package info.loenwind.enderioaddons.machine.drain;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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
