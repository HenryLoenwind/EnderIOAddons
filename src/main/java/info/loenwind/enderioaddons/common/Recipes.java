package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
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

	@ItemStackHolder(value="EnderIO:itemMachinePart", meta=0)
	public static final ItemStack machineChassi = null;
	@ItemStackHolder(value="EnderIO:blockTank", meta=0)
	public static final ItemStack basicTank = null;
	@ItemStackHolder(value="EnderIO:itemAlloy", meta=0)
	public static final ItemStack electricSteel = null;

	

	public Recipes() {}

	@Override
	public void init(FMLPreInitializationEvent event) {
	}

	@Override
	public void init(FMLInitializationEvent event) {
      //Drain
      if (Config.drainEnabled) {
  		Log.info("EnderIO:itemMachinePart"+machineChassi);
  		Log.info("EnderIO:blockTank"+basicTank);
  		Log.info("EnderIO:itemAlloy"+electricSteel);
        ItemStack drain = new ItemStack(BlockDrain.blockDrain, 1, 0);
        GameRegistry.addRecipe(new ShapedOreRecipe(drain, 
        		"btb", "pmp", "eve", 
        		'm', machineChassi, 
        		't', basicTank, 
        		'p', Blocks.piston, 
        		'b', Items.bucket, 
        		'e', electricSteel, 
        		'v', Items.cauldron));
      }

	}

	@Override
	public void init(FMLPostInitializationEvent event) {
	}

}
