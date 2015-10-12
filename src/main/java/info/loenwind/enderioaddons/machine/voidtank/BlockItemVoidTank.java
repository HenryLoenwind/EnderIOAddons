package info.loenwind.enderioaddons.machine.voidtank;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.EnderIOTab;

public class BlockItemVoidTank extends ItemBlockWithMetadata implements IAdvancedTooltipProvider {

  public BlockItemVoidTank() {
    this(BlockVoidTank.blockVoidTank);
  }

  public BlockItemVoidTank(Block block) {
    super(block, block);
    setHasSubtypes(false);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    BlockVoidTank.blockVoidTank.addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    BlockVoidTank.blockVoidTank.addBasicEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    BlockVoidTank.blockVoidTank.addDetailedEntries(itemstack, entityplayer, list, flag);
  }

}
