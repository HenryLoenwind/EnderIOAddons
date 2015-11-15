package info.loenwind.enderioaddons.machine.part;

import info.loenwind.enderioaddons.machine.afarm.AgriDetector;

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

public class ItemMachinePart extends Item {

  public static final ModObject ModObject_itemMachinePart = EnumHelper.addEnum(ModObject.class, "itemMachineParts", new Class<?>[0], new Object[0]);
  public static ItemMachinePart itemMachinePart;

  private final IIcon[] icons;

  public static ItemMachinePart create() {
    itemMachinePart = new ItemMachinePart();
    itemMachinePart.init();
    return itemMachinePart;
  }

  private ItemMachinePart() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject_itemMachinePart.unlocalisedName);

    icons = new IIcon[MachinePart.values().length];
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject_itemMachinePart.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamage(int damage) {
    return icons[MathHelper.clamp_int(damage, 0, MachinePart.values().length - 1)];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    int numParts = MachinePart.values().length;
    for (int i = 0; i < numParts; i++) {
      if (!MachinePart.values()[i].renderAsFrameMachine) {
        if (i != MachinePart.AFARMINFO.ordinal() || !AgriDetector.hasAgri) {
          icons[i] = IIconRegister.registerIcon(MachinePart.values()[i].iconKey);
        } else {
          icons[i] = icons[MachinePart.SIMPLEMAGNET.ordinal()];
        }
      }
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, MachinePart.values().length - 1);
    return MachinePart.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < MachinePart.values().length; ++j) {
      if ((AgriDetector.hasAgri && j == MachinePart.AFARMINFO.ordinal()) || (!AgriDetector.hasAgri && MachinePart.isAgri(j))) {
        continue;
      }
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

}
