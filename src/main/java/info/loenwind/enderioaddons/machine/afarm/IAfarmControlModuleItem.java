package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;
import net.minecraft.item.ItemStack;

public interface IAfarmControlModuleItem {

  IAfarmControlModule getWorker(ItemStack stack);

}
