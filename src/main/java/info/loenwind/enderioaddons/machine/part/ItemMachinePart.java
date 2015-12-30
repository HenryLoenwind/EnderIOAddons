package info.loenwind.enderioaddons.machine.part;

import info.loenwind.enderioaddons.config.ConfigHandler;
import info.loenwind.enderioaddons.machine.afarm.AgriDetector;
import info.loenwind.enderioaddons.machine.waterworks.engine.ConfigProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import org.apache.commons.io.IOUtils;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;

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
    try {
      readConfig();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    if (stack.getItemDamage() == MachinePart.COOKIE.ordinal()) {
      if (!world.isRemote) {
        String text = fortunes.get(world.rand.nextInt(fortunes.size()));
        player.addChatMessage(new ChatComponentText(text));
        ItemStack strip = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.COOKIESTRIP.ordinal());
        strip.setStackDisplayName(text);
        ItemStack result = stack.copy();
        result.stackSize--;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
          ItemStack inventoryItem = player.inventory.mainInventory[i];
          if (ItemUtil.areStackMergable(inventoryItem, strip) && inventoryItem.stackSize < inventoryItem.getMaxStackSize()) {
            strip.stackSize += inventoryItem.stackSize;
            player.inventory.setInventorySlotContents(i, strip);
            return result;
          }
        }
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
          if (player.inventory.mainInventory[i] == null) {
            player.inventory.setInventorySlotContents(i, strip);
            return result;
          }
        }
        Util.dropItems(world, strip, player.posX, player.posY + 0.5D, player.posZ, true);
        return result;
      }
    }
    return stack;
  }

  private static final List<String> fortunes = new ArrayList<>();

  private static void readConfig() throws IOException {
    final String fileName = "fortunes.txt";
    File configFile = new File(ConfigHandler.configDirectory, fileName);

    if (configFile.exists()) {
      readConfigFile(configFile);
    }

    InputStream defaultFile = ConfigProvider.class.getResourceAsStream("/assets/enderioaddons/config/" + fileName);
    if (defaultFile == null) {
      throw new IOException("Could not get resource /assets/enderioaddons/config/" + fileName + " from classpath. ");
    }

    readConfigFile(defaultFile);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      for (String string : fortunes) {
        writer.append(string);
        writer.append("\n");
      }
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  private static void readConfigFile(File file) throws IOException {
    FileInputStream fis = new FileInputStream(file);
    readConfigFile(fis);
  }

  private static void readConfigFile(InputStream file) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(file));
    String line = null;
    while ((line = br.readLine()) != null) {
      fortunes.add(line);
    }
    br.close();
  }

}
