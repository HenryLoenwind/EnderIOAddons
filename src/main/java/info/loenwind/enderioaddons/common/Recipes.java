package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
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

  @ItemStackHolder(value = "EnderIO:itemMachinePart", meta = 0)
  public static final ItemStack machineChassi = null;
  @ItemStackHolder(value = "EnderIO:blockTank", meta = 0)

  public static final ItemStack basicTank = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 0)

  public static final ItemStack electricSteel = null;
  @ItemStackHolder(value = "EnderIO:itemAlloy", meta = 6)
  public static final ItemStack darkSteel = null;

  @ItemStackHolder(value = "EnderIO:itemMaterial", meta = 0)
  public static final ItemStack silicon = null;
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
    //Drain
    if (Config.drainEnabled) {
      ItemStack drain = new ItemStack(BlockDrain.blockDrain, 1, 0);
      GameRegistry.addRecipe(new ShapedOreRecipe(drain, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p', Blocks.piston, 'b', Items.bucket, 'e',
          electricSteel, 'v', Items.cauldron));
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

    //Cobbleworks
    ItemStack machineFrame = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MACHINE_FRAME.ordinal());
    ItemStack frameTank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FRAME_TANK.ordinal());
    ItemStack frameTanks = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FRAME_TANKS.ordinal());
    ItemStack machineFrameTank = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.MACHINE_FRAME_TANK.ordinal());
    ItemStack cobbleController = new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.COBBLE_CONTROLLER.ordinal());
    ItemStack cobbleworks = new ItemStack(BlockCobbleworks.blockCobbleworks);

    GameRegistry.addShapedRecipe(machineFrame, "dsd", "s s", "dsd", 's', electricSteel, 'd', darkSteel);
    GameRegistry.addShapedRecipe(frameTank, "scs", "c c", "scs", 's', silicon, 'c', clearGlass);
    GameRegistry.addShapelessRecipe(frameTanks, frameTank, frameTank, frameTank, frameTank);
    GameRegistry.addShapelessRecipe(machineFrameTank, machineFrame, frameTank, frameTank, frameTank, frameTank);
    GameRegistry.addShapelessRecipe(machineFrameTank, machineFrame, frameTanks);
    GameRegistry.addShapedRecipe(cobbleController, "sis", "lMw", "pzp", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'z', zombieBit, 'l',
        Items.lava_bucket, 'w', Items.water_bucket, 'p', crystal);
    GameRegistry.addShapedRecipe(cobbleController, "sis", "wMl", "pzp", 'i', Items.iron_ingot, 's', electricSteel, 'M', machineChassi, 'z', zombieBit, 'l',
        Items.lava_bucket, 'w', Items.water_bucket, 'p', crystal);
    GameRegistry.addShapelessRecipe(cobbleworks, machineFrameTank, cobbleController);
    GameRegistry.addShapelessRecipe(cobbleworks, machineFrame, frameTank, frameTank, frameTank, frameTank, cobbleController);
    GameRegistry.addShapelessRecipe(cobbleworks, machineFrame, frameTanks, cobbleController);

  }

  @Override
  public void init(FMLPostInitializationEvent event) {
  }

}
