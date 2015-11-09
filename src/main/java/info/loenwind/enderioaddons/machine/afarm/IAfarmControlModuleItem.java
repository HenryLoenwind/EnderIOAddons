package info.loenwind.enderioaddons.machine.afarm;

import net.minecraft.item.ItemStack;

public interface IAfarmControlModuleItem {

  IAfarmControlModule getWorker(ItemStack stack);

}
