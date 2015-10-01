package info.loenwind.enderioaddons.nei;

import static info.loenwind.enderioaddons.config.Config.cobbleWorksEnabled;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.fluid.Fluids;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.framework.AbstractBlockFramework;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.hideItem(new ItemStack(AbstractBlockFramework.blockDummy, 1, OreDictionary.WILDCARD_VALUE));
    if (!cobbleWorksEnabled.getBoolean()) {
      API.hideItem(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.COBBLE_CONTROLLER.ordinal()));
      API.hideItem(new ItemStack(BlockCobbleworks.blockCobbleworks, 1, OreDictionary.WILDCARD_VALUE));
    }
    if (!Config.waterWorksEnabled.getBoolean()) {
      API.hideItem(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.HEATING_ELEMENT.ordinal()));
      API.hideItem(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FILTER_ELEMENT.ordinal()));
      API.hideItem(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.WATER_CONTROLLER.ordinal()));
      API.hideItem(new ItemStack(BlockWaterworks.blockWaterworks, 1, OreDictionary.WILDCARD_VALUE));
      for (Fluids fluid : Fluids.values()) {
        API.hideItem(new ItemStack(fluid.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
        API.hideItem(new ItemStack(fluid.getBucket(), 1, OreDictionary.WILDCARD_VALUE));
      }
    }
    if (!Config.impulseHopperEnabled.getBoolean()) {
      API.hideItem(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.IHOPPER_CONTROLLER.ordinal()));
      API.hideItem(new ItemStack(BlockIHopper.blockIHopper, 1, OreDictionary.WILDCARD_VALUE));
    }
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
