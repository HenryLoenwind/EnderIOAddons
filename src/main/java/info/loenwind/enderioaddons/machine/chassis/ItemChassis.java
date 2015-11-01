package info.loenwind.enderioaddons.machine.chassis;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemChassis extends ItemBlockWithMetadata {

  public ItemChassis(Block block) {
    super(block, block);
  }

  @Override
  public String getUnlocalizedName(ItemStack p_77667_1_) {
    return super.getUnlocalizedName() + "." + p_77667_1_.getItemDamage();
  }
}
