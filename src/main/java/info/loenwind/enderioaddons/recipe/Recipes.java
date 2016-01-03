package info.loenwind.enderioaddons.recipe;

import static crazypants.util.RecipeUtil.addShaped;
import static crazypants.util.RecipeUtil.addShapeless;
import static info.loenwind.enderioaddons.config.Config.farmSeedlessRecipesEnabled;
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
import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.afarm.AgriDetector;
import info.loenwind.enderioaddons.machine.afarm.BlockAfarm;
import info.loenwind.enderioaddons.machine.afarm.item.ItemModule;
import info.loenwind.enderioaddons.machine.afarm.item.Module;
import info.loenwind.enderioaddons.machine.chassis.BlockChassis;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import info.loenwind.enderioaddons.machine.rlever.BlockRLever;
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

  @ItemStackHolder(value = "EnderIO:blockCapBank", meta = 0)
  public static final ItemStack capBankCreative = null;

  @ItemStackHolder(value = "EnderIO:blockFusedQuartz", meta = 1)
  public static final ItemStack clearGlass = null;

  @ItemStackHolder(value = "EnderIO:itemFrankenSkull", meta = 1)
  public static final ItemStack zombieController = null;
  @ItemStackHolder(value = "EnderIO:itemFrankenSkull", meta = 2)
  public static final ItemStack frankenZombie = null;

  @ItemStackHolder(value = "EnderIO:blockEndermanSkull", meta = 0)
  public static final ItemStack endermanSkull = null;

  @ItemStackHolder(value = "EnderIO:itemBasicCapacitor", meta = 0)
  public static final ItemStack capacitor1 = null;
  @ItemStackHolder(value = "EnderIO:itemBasicCapacitor", meta = 1)
  public static final ItemStack capacitor2 = null;
  @ItemStackHolder(value = "EnderIO:itemBasicCapacitor", meta = 2)
  public static final ItemStack capacitor8 = null;

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

    // Fortune Cookie
    ItemStack fortune = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.COOKIE.ordinal());
    addShaped(fortune, "cgc", "cpc", "gcg", 'c', Items.cookie, 'g', "nuggetGold", 'p', Items.paper);

    // Drain
    if (Config.drainEnabled.getBoolean()) {
      addShaped(new ItemStack(blockDrain, 1, 0), "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e',
          electricSteel, 'v', Items.cauldron);
    }

    // Food Drain
    if (Config.foodDrainEnabled.getBoolean()) {
      ItemStack plate = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FOODPLATE.ordinal());
      ItemStack plate2 = new ItemStack(ItemMachinePart.itemMachinePart, 2, MachinePart.FOODPLATE.ordinal());
      ItemStack grill = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FOODGRILL.ordinal());
      ItemStack grill3 = new ItemStack(ItemMachinePart.itemMachinePart, 3, MachinePart.FOODGRILL.ordinal());
      if (Config.drainEnabled.getBoolean()) {
        addShaped(plate2, "ppp", "ggg", "sss", 's', electricSteel, 'g', "slimeball", 'p', "paneGlassWhite");
        addShaped(grill3, "ppp", "ggg", "sss", 's', Blocks.iron_bars, 'g', "slimeball", 'p', "paneGlassWhite");
        addShaped(grill3, "ppp", "ggg", "sss", 's', darkSteelBars, 'g', "slimeball", 'p', "paneGlassWhite");
        addShaped(new ItemStack(blockDrain, 1, 1), "ggg", "gDg", " p ", 'D', new ItemStack(blockDrain, 1, 0), 'g', grill, 'p', plate);
      } else {
        addShaped(plate, "ppp", "ggg", "sss", 's', electricSteel, 'g', "slimeball", 'p', "paneGlassWhite");
        addShaped(grill, "ppp", "ggg", "sss", 's', Blocks.iron_bars, 'g', "slimeball", 'p', "paneGlassWhite");
        addShaped(grill, "ppp", "ggg", "sss", 's', darkSteelBars, 'g', "slimeball", 'p', "paneGlassWhite");
        addShaped(new ItemStack(blockDrain, 1, 0), "btb", "pmp", "pvg", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'v',
            Items.cauldron, 'g', grill, 'p', plate);
      }
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
        ItemStack scs = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.SCS.ordinal());
        ItemStack mcs = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MCS.ordinal());
        ItemStack lcs = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.LCS.ordinal());
        ItemStack clhp = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.CLHP.ordinal());
        ItemStack clhp8 = new ItemStack(ItemMachinePart.itemMachinePart, 8, MachinePart.CLHP.ordinal());
        ItemStack seed = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.SEED.ordinal());
        ItemStack moduleBase = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FCM_BASE.ordinal());
        ItemStack moduleIQ = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FCM_IQ.ordinal());
        ItemStack farm = new ItemStack(BlockAfarm.blockAfarm);
        ItemStack induRake = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.IRAKE.ordinal());
        ItemStack brokenRakeWood = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.RAKE_BR1.ordinal());
        ItemStack brokenRakeIron = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.RAKE_BR2.ordinal());

        addShaped(capacitor1, " s ", "sms", "srs", 's', scs, 'm', mcs, 'r', "dustRedstone");
        addShaped(capacitor2, " s ", "sls", "srs", 's', scs, 'l', lcs, 'r', "dustRedstone");
        addShaped(capacitor8, " s ", "mlm", "srs", 's', scs, 'm', mcs, 'l', lcs, 'r', "dustRedstone");

        if (crazypants.enderio.config.Config.useHardRecipes) {
          addShaped(clhp, "sms", "ccc", "eee", 's', scs, 'm', mcs, 'c', conductiveIron, 'e', electricSteel);
          addShaped(seed, "eee", "bcb", "shs", 'e', "listAllseed", 'b', "dyeWhite", 'c', capacitor8, 's', Blocks.soul_sand, 'h', endermanSkull);
        } else {
          addShaped(clhp8, "sms", "ccc", "eee", 's', scs, 'm', mcs, 'c', conductiveIron, 'e', electricSteel);
          addShaped(mcs, "sss", "sss", "sss", 's', scs);
          addShaped(seed, "eee", "bcb", "shs", 'e', "listAllseed", 'b', "dyeWhite", 'c', capacitor1, 's', Blocks.soul_sand, 'h', Blocks.dirt);
        }

        if (farmSeedlessRecipesEnabled.getBoolean()) {
          addShaped(moduleBase, " np", "ncn", "pn ", 'n', agriNugget, 'p', chassiParts, 'c', crops);
        }

        addShaped(moduleBase, " np", "ncn", "pn ", 'n', clhp, 'p', chassiParts, 'c', crops);
        addShaped(moduleIQ, "bbb", "bzb", "bbb", 'b', moduleBase, 'z', zombieBit);
        addShaped(farm, "ehe", "eCe", "cMc", 'e', electricSteel, 'h', Items.diamond_hoe, 'C', machineChassi, 'M', moduleIQ, 'c', crystal);
        addShaped(induRake, "bb", " d", " d", 'b', darkSteelBars, 'd', darkSteel);
        addShaped(induRake, "bb", "d ", "d ", 'b', darkSteelBars, 'd', darkSteel);
        if (handRake_wood != null) {
          addShapeless(handRake_wood, brokenRakeWood, "stickWood", brokenRakeWood);
          addShapeless(handRake_wood, brokenRakeWood, "woodStick", brokenRakeWood);
        } else {
          Log.warn("Failed to find AgriCraft's Wooden Hand Rake. Some recipes will be missing!");
        }
        if (handRake_iron != null) {
          addShapeless(handRake_iron, brokenRakeIron, "nuggetIron", brokenRakeIron);
        } else {
          Log.warn("Failed to find AgriCraft's Iron Hand Rake. Some recipes will be missing!");
        }

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
        if (handRake_wood != null) {
          addShapeless(WEED, weeds, moduleBase, handRake_wood);
        }
        if (handRake_iron != null) {
          addShapeless(WEED, weeds, moduleBase, handRake_iron);
        }
        addShapeless(WEED, weeds, moduleBase, induRake);
        addShapeless(EJECTSEEDS, "listAllseed", moduleBase, "blockHopper");
        addShapeless(BESTONLY, EJECTSEEDS, moduleIQ, magGlass);
      }
    }

    // Lever
    addShapeless(BlockRLever.blockRLever10, Blocks.lever, "dustRedstone");

    addShapeless(BlockRLever.blockRLever30, BlockRLever.blockRLever10, "dustRedstone");
    addShapeless(BlockRLever.blockRLever30, Blocks.lever, "dustRedstone", "dustRedstone");

    addShapeless(BlockRLever.blockRLever60, BlockRLever.blockRLever10, "dustRedstone", "dustRedstone");
    addShapeless(BlockRLever.blockRLever60, BlockRLever.blockRLever30, "dustRedstone");
    addShapeless(BlockRLever.blockRLever60, Blocks.lever, "dustRedstone", "dustRedstone", "dustRedstone");

    addShapeless(BlockRLever.blockRLever300, BlockRLever.blockRLever10, "dustRedstone", "dustRedstone", "dustRedstone");
    addShapeless(BlockRLever.blockRLever300, BlockRLever.blockRLever30, "dustRedstone", "dustRedstone");
    addShapeless(BlockRLever.blockRLever300, BlockRLever.blockRLever60, "dustRedstone");
    addShapeless(BlockRLever.blockRLever300, Blocks.lever, "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone");

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
