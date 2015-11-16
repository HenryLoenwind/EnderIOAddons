package info.loenwind.enderioaddons.recipe;

import static crazypants.util.RecipeUtil.addShaped;
import static crazypants.util.RecipeUtil.addShapeless;
import static info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks.blockCobbleworks;
import static info.loenwind.enderioaddons.machine.drain.BlockDrain.blockDrain;
import static info.loenwind.enderioaddons.machine.ihopper.BlockIHopper.blockIHopper;
import static info.loenwind.enderioaddons.machine.magcharger.BlockMagCharger.blockMagCharger;
import static info.loenwind.enderioaddons.machine.niard.BlockNiard.blockNiard;
import static info.loenwind.enderioaddons.machine.pmon.BlockPMon.blockPMon;
import static info.loenwind.enderioaddons.machine.tcom.BlockTcom.blockTcom;
import static info.loenwind.enderioaddons.machine.voidtank.BlockVoidTank.blockVoidTank;
import static info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks.blockWaterworks;
import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.afarm.AgriDetector;
import info.loenwind.enderioaddons.machine.afarm.BlockAfarm;
import info.loenwind.enderioaddons.machine.afarm.item.ItemModule;
import info.loenwind.enderioaddons.machine.afarm.item.Module;
import info.loenwind.enderioaddons.machine.chassis.BlockChassis;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
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

  @ItemStackHolder(value = "AgriCraft:handRake", meta = 0)
  public static final ItemStack handRake_wood = null;
  @ItemStackHolder(value = "AgriCraft:handRake", meta = 1)
  public static final ItemStack handRake_iron = null;
  @ItemStackHolder(value = "AgriCraft:magnifyingGlass")
  public static final ItemStack magGlass = null;
  @ItemStackHolder(value = "AgriCraft:cropsItem")
  public static final ItemStack crops = null;
  @ItemStackHolder(value = "AgriCraft:seedAnalyzer")
  public static final ItemStack seeAnalyzer = null;

  @ItemStackHolder(value = "minecraft:tallgrass", meta = 1)
  public static final ItemStack weeds = null;

  public Recipes() {
  }

  @Override
  public void init(FMLPreInitializationEvent event) {
  }

  @Override
  public void init(FMLInitializationEvent event) {
    ItemStack zombieBit;
    ItemStack crystal;
    Object agriNugget;
    if (crazypants.enderio.config.Config.useHardRecipes) {
      zombieBit = frankenZombie;
      crystal = enderCrystal;
      agriNugget = "cropFerranium";
      if (!OreDictionary.doesOreNameExist((String) agriNugget)) {
        agriNugget = darkSteel;
      }
    } else {
      zombieBit = zombieController;
      crystal = pulsatingCrystal;
      agriNugget = "nuggetIron";
      if (!OreDictionary.doesOreNameExist((String) agriNugget)) {
        agriNugget = "ingotIron";
      }
    }

    // Drain
    if (Config.drainEnabled.getBoolean()) {
      addShaped(blockDrain, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e', electricSteel, 'v',
          Items.cauldron);
    }

    // Niard
    if (Config.niardEnabled.getBoolean()) {
      addShaped(blockNiard, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e', electricSteel, 'v',
          darkSteelBars);
    }

    // Void Tank
    if (Config.voidTankEnabled.getBoolean()) {
      addShaped(blockVoidTank, "omo", "sfs", "oto", 'm', machineChassi, 't', advancedTank, 'o', "blockObsidian", 'f', Items.flint_and_steel, 's', darkSteel);
    }

    // Power Monitor
    if (Config.pMonEnabled.getBoolean()) {
      addShaped(blockPMon, "xxx", "xpx", "123", 'p', blockPowerMonitor, '1', "dyeRed", '2', "dyeYellow", '3', "dyeGreen", 'x',
          new ItemStack(Blocks.wool, 1, 15));
    }

    // Frame parts
    if (isAnyFrameworkMachineEnabled()) {
      ItemStack machineFrame = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MACHINE_FRAME.ordinal());
      addShaped(machineFrame, "dsd", "s s", "dsd", 's', electricSteel, 'd', darkSteel);

      if (isAnyFrameworkMachineWithTanksEnabled()) {
        ItemStack frameTank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FRAME_TANK.ordinal());
        addShaped(frameTank, "scs", "c c", "scs", 's', silicon, 'c', clearGlass);

        ItemStack frameTanks = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FRAME_TANKS.ordinal());
        addShapeless(frameTanks, frameTank, frameTank, frameTank, frameTank);

        ItemStack machineFrameTank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MACHINE_FRAME_TANK.ordinal());
        addShapeless(machineFrameTank, machineFrame, frameTank, frameTank, frameTank, frameTank);
        addShapeless(machineFrameTank, machineFrame, frameTanks);

        // Cobbleworks
        if (Config.cobbleWorksEnabled.getBoolean()) {
          ItemStack cobbleController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.COBBLE_CONTROLLER.ordinal());
          addShaped(cobbleController, "sis", "lMw", "pzp", 'i', "ingotIron", 's', electricSteel, 'M', machineChassi, 'z', zombieBit, 'l', Items.lava_bucket,
              'w', Items.water_bucket, 'p', crystal);
          addShaped(cobbleController, "sis", "wMl", "pzp", 'i', "ingotIron", 's', electricSteel, 'M', machineChassi, 'z', zombieBit, 'l', Items.lava_bucket,
              'w', Items.water_bucket, 'p', crystal);

          addShapeless(blockCobbleworks, machineFrameTank, cobbleController);
          addShapeless(blockCobbleworks, machineFrame, frameTank, frameTank, frameTank, frameTank, cobbleController);
          addShapeless(blockCobbleworks, machineFrame, frameTanks, cobbleController);
        }

        // Waterworks
        if (Config.waterWorksEnabled.getBoolean()) {
          ItemStack heatingElement = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.HEATING_ELEMENT.ordinal());
          addShaped(heatingElement, "ccs", "srs", "scc", 's', silicon, 'c', conductiveIron, 'r', blockRedstoneAlloy);

          ItemStack filterElement = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FILTER_ELEMENT.ordinal());
          addShaped(filterElement, "bbb", "ccc", "bbb", 'b', darkSteelBars, 'c', binderComposite);

          ItemStack waterController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.WATER_CONTROLLER.ordinal());
          addShaped(waterController, "sis", "fMf", "fhf", 'i', "ingotIron", 's', electricSteel, 'M', machineChassi, 'f', filterElement, 'h', heatingElement);

          addShapeless(blockWaterworks, machineFrameTank, waterController);
          addShapeless(blockWaterworks, machineFrame, frameTank, frameTank, frameTank, frameTank, waterController);
          addShapeless(blockWaterworks, machineFrame, frameTanks, waterController);
        }

        if (Config.tcomEnabled.getBoolean()) {
          ItemStack tray = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.TRAY.ordinal());
          addShaped(tray, "   ", "i i", "did", 'i', "ingotIron", 'd', darkSteel);

          ItemStack pylon = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.PYLON.ordinal());
          addShaped(pylon, "ese", " E ", "ese", 'e', electricSteel, 's', soularium, 'E', Blocks.enchanting_table);

          ItemStack pylontank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.PYLONTANK.ordinal());
          addShapeless(pylontank, frameTank, pylon);

          ItemStack tcomController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.TCOM_CONTROLLER.ordinal());
          addShaped(tcomController, "sCs", "pMa", "czc", 'M', machineChassi, 'z', zombieBit, 'c', crystal, 's', electricSteel, 'C', Blocks.chest, 'p',
              Blocks.piston, 'a', new ItemStack(Blocks.anvil, 1, 0));

          addShapeless(blockTcom, machineFrame, tcomController, pylontank, tray, tray, tray, tray, tray, tray);
        }
      }

      // Impulse Hopper
      if (Config.impulseHopperEnabled.getBoolean()) {
        ItemStack ihopperController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.IHOPPER_CONTROLLER.ordinal());
        addShaped(ihopperController, "sis", "hMt", "tzh", 'i', "ingotIron", 's', electricSteel, 'M', machineChassi, 'z', zombieBit, 'h', hopper, 't',
            redstoneTorch);
        addShaped(ihopperController, "sis", "tMh", "hzt", 'i', "ingotIron", 's', electricSteel, 'M', machineChassi, 'z', zombieBit, 'h', hopper, 't',
            redstoneTorch);

        addShapeless(blockIHopper, machineFrame, ihopperController, hopper, hopper, hopper);
      }

    }

    boolean farmEnabled = Config.farmEnabled.getBoolean() && AgriDetector.hasAgri;
    
    if (farmEnabled || Config.flagEnabled.getBoolean() || Config.magcEnabled.getBoolean() || Config.decoBlockEnabled.getBoolean()) {
      ItemStack chassiParts = new ItemStack(ItemMachinePart.itemMachinePart, 8, MachinePart.CHASSIPARTS.ordinal());
      addShaped(chassiParts, "iii", "iMi", "iii", 'M', machineChassi, 'i', "ingotIron");

      if (Config.flagEnabled.getBoolean() || Config.magcEnabled.getBoolean()) {
        ItemStack simpleMagnet = new ItemStack(ItemMachinePart.itemMachinePart, 2, MachinePart.SIMPLEMAGNET.ordinal());
        addShaped(simpleMagnet, "ccc", "c c", "s s", 's', electricSteel, 'c', conductiveIron);

        // Magnetic Flag
        if (Config.flagEnabled.getBoolean()) {
          ItemStack flagParts = new ItemStack(ItemMachinePart.itemMachinePart, 8, MachinePart.FLAGPARTS.ordinal());
          addShaped(flagParts, "sRR", "sRR", "s  ", 's', electricSteel, 'R', new ItemStack(Blocks.wool, 1, 14));
          ItemStack flags = new ItemStack(BlockFlag.blockFlag, 16, 1);
          addShaped(flags, " f ", " f ", "csc", 'f', flagParts, 's', simpleMagnet, 'c', chassiParts);
          ItemStack flag = new ItemStack(BlockFlag.blockFlag, 1, 0);
          addShapeless(flag, flag);
        }

        // Magnetic Charger
        if (Config.flagEnabled.getBoolean()) {
          addShaped(blockMagCharger, "CsC", "sMs", "csc", 'M', machineChassi, 'C', conductiveIron, 's', simpleMagnet, 'c', chassiParts);
        }
      }

      // Decoration Block
      if (Config.decoBlockEnabled.getBoolean()) {
        ItemStack[] deco = new ItemStack[16];
        for (int i = 0; i < 16; i++) {
          deco[i] = new ItemStack(BlockChassis.blockChassis, 1, i);
        }
        addShaped(deco[0], " p ", "p p", " p ", 'p', chassiParts);
        addShaped(deco[1], " i ", "idi", " i ", 'd', deco[0], 'i', "ingotIron");
        addShaped(deco[1], "ipi", "p p", "ipi", 'p', chassiParts, 'i', "ingotIron");
        addShaped(deco[1], "pip", "i i", "pip", 'p', chassiParts, 'i', "ingotIron");
        addShapeless(deco[2], deco[1]);
        addShapeless(deco[3], deco[2]);
        addShapeless(deco[4], deco[3]);
        addShapeless(deco[5], deco[4]);
        addShapeless(deco[6], deco[5]);
        addShapeless(deco[1], deco[6]);

        addShaped(deco[7], " i ", "idi", " i ", 'd', deco[0], 'i', soularium);
        addShaped(deco[7], "ipi", "p p", "ipi", 'p', chassiParts, 'i', soularium);
        addShaped(deco[7], "pip", "i i", "pip", 'p', chassiParts, 'i', soularium);
        addShapeless(deco[8], deco[7]);
        addShapeless(deco[9], deco[8]);
        addShapeless(deco[10], deco[9]);
        addShapeless(deco[11], deco[10]);
        addShapeless(deco[12], deco[11]);
        addShapeless(deco[7], deco[12]);

        addShapeless(deco[13], deco[1], "dyeRed");
        addShapeless(deco[1], deco[13], Items.water_bucket);

        addShaped(deco[14], " i ", "idi", " i ", 'd', deco[0], 'i', Blocks.iron_bars);
        addShaped(deco[14], "ipi", "p p", "ipi", 'p', chassiParts, 'i', Blocks.iron_bars);
        addShaped(deco[14], "pip", "i i", "pip", 'p', chassiParts, 'i', Blocks.iron_bars);

        addShaped(deco[15], " i ", "idi", " i ", 'd', deco[0], 'i', clearGlass);
        addShaped(deco[15], "ipi", "p p", "ipi", 'p', chassiParts, 'i', clearGlass);
        addShaped(deco[15], "pip", "i i", "pip", 'p', chassiParts, 'i', clearGlass);
      }
      
      // Agri Farm
      if (farmEnabled) {
        ItemStack moduleBase =  new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FCM_BASE.ordinal());
        addShaped(moduleBase, " np", "ncn", "pn ", 'n', agriNugget, 'p', chassiParts, 'c', crops);
        ItemStack moduleIQ =  new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FCM_IQ.ordinal());
        addShaped(moduleIQ, "bbb", "bzb", "bbb", 'b', moduleBase, 'z', zombieBit);
        ItemStack farm = new ItemStack(BlockAfarm.blockAfarm);
        addShaped(farm, "ehe", "eCe", "cMc", 'e', electricSteel, 'h', Items.diamond_hoe, 'C', machineChassi, 'M', moduleIQ, 'c', crystal);
        ItemStack induRake = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.IRAKE.ordinal());
        addShaped(induRake, "bb", " d", " d", 'b', darkSteelBars, 'd', darkSteel);
        addShaped(induRake, "bb", "d ", "d ", 'b', darkSteelBars, 'd', darkSteel);
        ItemStack brokenRakeWood = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.RAKE_BR1.ordinal());
        ItemStack brokenRakeIron = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.RAKE_BR2.ordinal());
        addShapeless(handRake_wood, brokenRakeWood, "stickWood", brokenRakeWood);
        addShapeless(handRake_wood, brokenRakeWood, "woodStick", brokenRakeWood);
        addShapeless(handRake_iron, brokenRakeIron, "nuggetIron", brokenRakeIron);

        ItemStack BREED = new ItemStack(ItemModule.itemModule, 1, Module.BREED.ordinal());
        ItemStack CROSSBREED = new ItemStack(ItemModule.itemModule, 1, Module.CROSSBREED.ordinal());
        ItemStack HARVESTSEEDS = new ItemStack(ItemModule.itemModule, 1, Module.HARVESTSEEDS.ordinal());
        ItemStack ANALYZESEEDS = new ItemStack(ItemModule.itemModule, 1, Module.ANALYZESEEDS.ordinal());
        ItemStack MULTIPLY = new ItemStack(ItemModule.itemModule, 1, Module.MULTIPLY.ordinal());
        ItemStack HARVESTUNANALYZED = new ItemStack(ItemModule.itemModule, 1, Module.HARVESTUNANALYZED.ordinal());
        ItemStack REPLACEBETTER = new ItemStack(ItemModule.itemModule, 1, Module.REPLACEBETTER.ordinal());
        ItemStack WEED = new ItemStack(ItemModule.itemModule, 1, Module.WEED.ordinal());
        ItemStack EJECTSEEDS = new ItemStack(ItemModule.itemModule, 1, Module.EJECTSEEDS.ordinal());
        ItemStack BESTONLY = new ItemStack(ItemModule.itemModule, 1, Module.BESTONLY.ordinal());

        addShapeless(BREED, crops, moduleBase, crops);
        addShapeless(CROSSBREED, "listAllseed", moduleIQ, crops);
        addShapeless(HARVESTSEEDS, "listAllseed", moduleBase, Items.diamond_hoe);
        addShapeless(ANALYZESEEDS, magGlass, moduleIQ, seeAnalyzer);
        addShapeless(MULTIPLY, crops, moduleIQ, crops);
        addShapeless(HARVESTUNANALYZED, Items.diamond_shovel, moduleBase, magGlass);
        addShapeless(REPLACEBETTER, Items.diamond_shovel, moduleIQ, magGlass);
        addShapeless(WEED, weeds, moduleBase, handRake_wood);
        addShapeless(EJECTSEEDS, "listAllseed", moduleBase, "blockHopper");
        addShapeless(BESTONLY, EJECTSEEDS, moduleIQ, magGlass);
      }
    }
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
