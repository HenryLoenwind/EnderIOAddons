package info.loenwind.enderioaddons.machine.afarm.item;

import info.loenwind.enderioaddons.machine.afarm.IAfarmControlModuleItem;
import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemModule extends Item implements IAfarmControlModuleItem {

  public static final ModObject ModObject_itemModule = EnumHelper.addEnum(ModObject.class, "itemModule", new Class<?>[0], new Object[0]);
  public static ItemModule itemMachinePart;

  private final IIcon[] icons;

  public static ItemModule create() {
    itemMachinePart = new ItemModule();
    itemMachinePart.init();
    return itemMachinePart;
  }

  private ItemModule() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject_itemModule.unlocalisedName);

    icons = new IIcon[Module.values().length];
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject_itemModule.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamage(int damage) {
    return icons[MathHelper.clamp_int(damage, 0, Module.values().length - 1)];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    int numParts = Module.values().length;
    for (int i = 0; i < numParts; i++) {
      icons[i] = IIconRegister.registerIcon(Module.values()[i].iconKey);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Module.values().length - 1);
    return Module.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Module.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public IAfarmControlModule getWorker(ItemStack stack) {
    int i = MathHelper.clamp_int(stack.getItemDamage(), 0, Module.values().length - 1);
    return Module.values()[i].module;
  }

}
