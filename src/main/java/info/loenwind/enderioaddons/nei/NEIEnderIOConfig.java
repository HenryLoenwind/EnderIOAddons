package info.loenwind.enderioaddons.nei;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.hideItem(new ItemStack(AbstractBlockFramework.blockDummy, 1, OreDictionary.WILDCARD_VALUE));
  }

  @Override
  public String getName() {
    return "Ender IO Addons NEI Plugin";
  }

  @Override
  public String getVersion() {
    return EnderIOAddons.VERSION;
  }

}
