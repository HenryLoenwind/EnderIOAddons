package info.loenwind.enderioaddons.recipe;

import info.loenwind.enderioaddons.common.InitAware;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.niard.BlockNiard;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import info.loenwind.enderioaddons.machine.voidtank.BlockVoidTank;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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

  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 0)
  public static final ItemStack electricSteel = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 4)
  public static final ItemStack conductiveIron = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 6)
  public static final ItemStack darkSteel = null;

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
      ItemStack niard = new ItemStack(BlockNiard.blockNiard, 1, OreDictionary.WILDCARD_VALUE);
      GameRegistry.addRecipe(new ShapedOreRecipe(niard, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e',
          electricSteel, 'v', darkSteelBars));
    }

    // Void Tank
    if (Config.voidTankEnabled.getBoolean()) {
      ItemStack voidTank = new ItemStack(BlockVoidTank.blockVoidTank, 1, OreDictionary.WILDCARD_VALUE);
      GameRegistry.addRecipe(new ShapedOreRecipe(voidTank, "omo", "sfs", "oto", 'm', machineChassi, 't', advancedTank, 'o', "blockObsidian", 'f',
          Items.flint_and_steel, 's', darkSteel));
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
  }

  private static boolean isAnyFrameworkMachineEnabled() {
    return Config.cobbleWorksEnabled.getBoolean() || Config.waterWorksEnabled.getBoolean() || Config.impulseHopperEnabled.getBoolean();
  }

  private static boolean isAnyFrameworkMachineWithTanksEnabled() {
    return Config.cobbleWorksEnabled.getBoolean() || Config.waterWorksEnabled.getBoolean();
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
