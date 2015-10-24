package info.loenwind.enderioaddons.recipe;

import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.niard.BlockNiard;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import info.loenwind.enderioaddons.machine.pmon.BlockPMon;
import info.loenwind.enderioaddons.machine.tcom.BlockTcom;
import info.loenwind.enderioaddons.machine.voidtank.BlockVoidTank;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ItemStackHolder;

public class Recipes implements InitAware {

  @ItemStackHolder(value = "minecraft:hopper", meta = 0)
  public static final ItemStack hopper = null;
  @ItemStackHolder(value = "minecraft:redstone_torch", meta = 0)
  public static final ItemStack redstoneTorch = null;

  @ItemStackHolder(value = "EnderIO:itemMachinePart", meta = 0)
  public static final ItemStack machineChassi = null;

  @ItemStackHolder(value = "EnderIO:blockTank", meta = 0)
  public static final ItemStack basicTank = null;
  @ItemStackHolder(value = "EnderIO:blockTank", meta = 1)
  public static final ItemStack advancedTank = null;

  @ItemStackHolder(value = "EnderIO:blockDarkIronBars", meta = 0)
  public static final ItemStack darkSteelBars = null;

  @ItemStackHolder(value = "EnderIO:blockIngotStorage", meta = 3)
  public static final ItemStack blockRedstoneAlloy = null;
  @ItemStackHolder(value = "EnderIO:blockIngotStorage", meta = 6)
  public static final ItemStack darkSteelBlock = null;

  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 0)
  public static final ItemStack electricSteel = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 4)
  public static final ItemStack conductiveIron = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 6)
  public static final ItemStack darkSteel = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 7)
  public static final ItemStack soularium = null;

  @ItemStackHolder(value = "EnderIO:itemMaterial", meta = 0)
  public static final ItemStack silicon = null;
  @ItemStackHolder(value = "EnderIO:itemMaterial", meta = 2)
  public static final ItemStack binderComposite = null;
  @ItemStackHolder(value = "EnderIO:itemMaterial", meta = 5)
  public static final ItemStack pulsatingCrystal = null;
  @ItemStackHolder(value = "EnderIO:itemMaterial", meta = 6)
  public static final ItemStack vibrantCrystal = null;
  @ItemStackHolder(value = "EnderIO:itemMaterial", meta = 8)
  public static final ItemStack enderCrystal = null;

  @ItemStackHolder(value = "EnderIO:blockFusedQuartz", meta = 1)
  public static final ItemStack clearGlass = null;

  @ItemStackHolder(value = "EnderIO:itemFrankenSkull", meta = 1)
  public static final ItemStack zombieController = null;
  @ItemStackHolder(value = "EnderIO:itemFrankenSkull", meta = 2)
  public static final ItemStack frankenZombie = null;

  @ItemStackHolder(value = "EnderIO:blockPowerMonitor", meta = 0)
  public static final ItemStack blockPowerMonitor = null;

  @ItemStackHolder(value = "EnderIO:item.darkSteel_helmet")
  public static final ItemStack darkSteel_helmet = null;
  @ItemStackHolder(value = "EnderIO:item.darkSteel_chestplate")
  public static final ItemStack darkSteel_chestplate = null;
  @ItemStackHolder(value = "EnderIO:item.darkSteel_leggings")
  public static final ItemStack darkSteel_leggings = null;
  @ItemStackHolder(value = "EnderIO:item.darkSteel_boots")
  public static final ItemStack darkSteel_boots = null;
  @ItemStackHolder(value = "EnderIO:item.darkSteel_sword")
  public static final ItemStack darkSteel_sword = null;
  @ItemStackHolder(value = "EnderIO:item.darkSteel_pickaxe")
  public static final ItemStack darkSteel_pickaxe = null;
  @ItemStackHolder(value = "EnderIO:item.darkSteel_axe")
  public static final ItemStack darkSteel_axe = null;

  public Recipes() {
  }

  @Override
  public void init(FMLPreInitializationEvent event) {
  }

  @Override
  public void init(FMLInitializationEvent event) {
    // Drain
    if (Config.drainEnabled.getBoolean()) {
      ItemStack drain = new ItemStack(BlockDrain.blockDrain, 1, 0);
      GameRegistry.addRecipe(new ShapedOreRecipe(drain, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e',
          electricSteel, 'v', Items.cauldron));
    }

    // Niard
    if (Config.niardEnabled.getBoolean()) {
      ItemStack niard = new ItemStack(BlockNiard.blockNiard, 1, 0);
      GameRegistry.addRecipe(new ShapedOreRecipe(niard, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e',
          electricSteel, 'v', darkSteelBars));
    }

    // Void Tank
    if (Config.voidTankEnabled.getBoolean()) {
      ItemStack voidTank = new ItemStack(BlockVoidTank.blockVoidTank, 1, 0);
      GameRegistry.addRecipe(new ShapedOreRecipe(voidTank, "omo", "sfs", "oto", 'm', machineChassi, 't', advancedTank, 'o', "blockObsidian", 'f',
          Items.flint_and_steel, 's', darkSteel));
    }

    // Power Monitor
    if (Config.pMonEnabled.getBoolean()) {
      ItemStack pmon = new ItemStack(BlockPMon.blockPMon, 1, 0);
      GameRegistry.addRecipe(new ShapedOreRecipe(pmon, "xxx", "xpx", "123", 'p', blockPowerMonitor, '1', "dyeRed", '2', "dyeYellow", '3', "dyeGreen", 'x',
          new ItemStack(Blocks.wool, 1, 15)));
    }

    ItemStack zombieBit;
    ItemStack crystal;
    if (crazypants.enderio.config.Config.useHardRecipes) {
      zombieBit = frankenZombie;
      crystal = enderCrystal;
    } else {
      zombieBit = zombieController;
      crystal = pulsatingCrystal;
    }

    // Frame parts
    if (isAnyFrameworkMachineEnabled()) {
      ItemStack machineFrame = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MACHINE_FRAME.ordinal());
      GameRegistry.addShapedRecipe(machineFrame, "dsd", "s s", "dsd", 's', electricSteel, 'd', darkSteel);

      if (isAnyFrameworkMachineWithTanksEnabled()) {
        ItemStack frameTank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FRAME_TANK.ordinal());
        GameRegistry.addShapedRecipe(frameTank, "scs", "c c", "scs", 's', silicon, 'c', clearGlass);

        ItemStack frameTanks = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FRAME_TANKS.ordinal());
        GameRegistry.addShapelessRecipe(frameTanks, frameTank, frameTank, frameTank, frameTank);

        ItemStack machineFrameTank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MACHINE_FRAME_TANK.ordinal());
        GameRegistry.addShapelessRecipe(machineFrameTank, machineFrame, frameTank, frameTank, frameTank, frameTank);
        GameRegistry.addShapelessRecipe(machineFrameTank, machineFrame, frameTanks);

        // Cobbleworks
        if (Config.cobbleWorksEnabled.getBoolean()) {
          ItemStack cobbleController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.COBBLE_CONTROLLER.ordinal());
          GameRegistry.addShapedRecipe(cobbleController, "sis", "lMw", "pzp", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'z', zombieBit,
              'l', Items.lava_bucket, 'w', Items.water_bucket, 'p', crystal);
          GameRegistry.addShapedRecipe(cobbleController, "sis", "wMl", "pzp", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'z', zombieBit,
              'l', Items.lava_bucket, 'w', Items.water_bucket, 'p', crystal);

          ItemStack cobbleworks = new ItemStack(BlockCobbleworks.blockCobbleworks);
          GameRegistry.addShapelessRecipe(cobbleworks, machineFrameTank, cobbleController);
          GameRegistry.addShapelessRecipe(cobbleworks, machineFrame, frameTank, frameTank, frameTank, frameTank, cobbleController);
          GameRegistry.addShapelessRecipe(cobbleworks, machineFrame, frameTanks, cobbleController);
        }

        // Waterworks
        if (Config.waterWorksEnabled.getBoolean()) {
          ItemStack heatingElement = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.HEATING_ELEMENT.ordinal());
          GameRegistry.addShapedRecipe(heatingElement, "ccs", "srs", "scc", 's', silicon, 'c', conductiveIron, 'r', blockRedstoneAlloy);

          ItemStack filterElement = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FILTER_ELEMENT.ordinal());
          GameRegistry.addShapedRecipe(filterElement, "bbb", "ccc", "bbb", 'b', darkSteelBars, 'c', binderComposite);

          ItemStack waterController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.WATER_CONTROLLER.ordinal());
          GameRegistry.addShapedRecipe(waterController, "sis", "fMf", "fhf", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'f', filterElement,
              'h', heatingElement);

          ItemStack waterworks = new ItemStack(BlockWaterworks.blockWaterworks);
          GameRegistry.addShapelessRecipe(waterworks, machineFrameTank, waterController);
          GameRegistry.addShapelessRecipe(waterworks, machineFrame, frameTank, frameTank, frameTank, frameTank, waterController);
          GameRegistry.addShapelessRecipe(waterworks, machineFrame, frameTanks, waterController);
        }

        if (Config.tcomEnabled.getBoolean()) {
          ItemStack tray = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.TRAY.ordinal());
          GameRegistry.addShapedRecipe(tray, "   ", "i i", "did", 'i', Items.iron_ingot, 'd', darkSteel);

          ItemStack pylon = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.PYLON.ordinal());
          GameRegistry.addShapedRecipe(pylon, "ese", " E ", "ese", 'e', electricSteel, 's', soularium, 'E', Blocks.enchanting_table);

          ItemStack pylontank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.PYLONTANK.ordinal());
          GameRegistry.addShapelessRecipe(pylontank, frameTank, pylon);

          ItemStack tcomController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.TCOM_CONTROLLER.ordinal());
          GameRegistry.addShapedRecipe(tcomController, "sCs", "pMa", "czc", 'M', machineChassi, 'z', zombieBit, 'c', crystal, 's', electricSteel, 'C',
              Blocks.chest, 'p', Blocks.piston, 'a', new ItemStack(Blocks.anvil, 1, 0));

          ItemStack tcom = new ItemStack(BlockTcom.blockTcom);
          GameRegistry.addShapelessRecipe(tcom, machineFrame, tcomController, pylontank, tray, tray, tray, tray, tray, tray);
        }
      }

      // Impulse Hopper
      if (Config.impulseHopperEnabled.getBoolean()) {
        ItemStack ihopperController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.IHOPPER_CONTROLLER.ordinal());
        GameRegistry.addShapedRecipe(ihopperController, "sis", "hMt", "tzh", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'z', zombieBit,
            'h', hopper, 't', redstoneTorch);
        GameRegistry.addShapedRecipe(ihopperController, "sis", "tMh", "hzt", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'z', zombieBit,
            'h', hopper, 't', redstoneTorch);

        ItemStack ihopper = new ItemStack(BlockIHopper.blockIHopper);
        GameRegistry.addShapelessRecipe(ihopper, machineFrame, ihopperController, hopper, hopper, hopper);
      }

    }

    // TODO Flag
    ItemStack simpleMagnet = new ItemStack(ItemMachinePart.itemMachinePart, 2, MachinePart.SIMPLEMAGNET.ordinal());
    GameRegistry.addShapedRecipe(simpleMagnet, "ccc", "c c", "s s", 's', electricSteel, 'c', conductiveIron);
    ItemStack chassiParts = new ItemStack(ItemMachinePart.itemMachinePart, 8, MachinePart.CHASSIPARTS.ordinal());
    GameRegistry.addShapedRecipe(chassiParts, "iii", "iMi", "iii", 'M', machineChassi, 'i', Items.iron_ingot);
    ItemStack flagParts = new ItemStack(ItemMachinePart.itemMachinePart, 8, MachinePart.FLAGPARTS.ordinal());
    GameRegistry.addShapedRecipe(flagParts, "sRR", "sRR", "s  ", 's', electricSteel, 'R', new ItemStack(Blocks.wool, 1, 14));
    ItemStack flags = new ItemStack(BlockFlag.blockFlag, 64, 1);
    GameRegistry.addShapedRecipe(flags, " f ", " f ", "csc", 'f', flagParts, 's', simpleMagnet, 'c', chassiParts);
    ItemStack flag = new ItemStack(BlockFlag.blockFlag, 1, 0);
    GameRegistry.addShapelessRecipe(flag, flag);
  }

  private static boolean isAnyFrameworkMachineEnabled() {
    return Config.cobbleWorksEnabled.getBoolean() || Config.waterWorksEnabled.getBoolean() || Config.impulseHopperEnabled.getBoolean()
        || Config.tcomEnabled.getBoolean();
  }

  private static boolean isAnyFrameworkMachineWithTanksEnabled() {
    return Config.cobbleWorksEnabled.getBoolean() || Config.waterWorksEnabled.getBoolean() || Config.tcomEnabled.getBoolean();
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
