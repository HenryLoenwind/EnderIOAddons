package info.loenwind.enderioaddons;

import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.drain.BlockDrain;

import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import test.TestX;
import test.Water;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ItemStackHolder;

@Mod(modid = EnderIOAddons.MODID, name = EnderIOAddons.MOD_NAME, version = EnderIOAddons.VERSION, dependencies = "required-after:EnderIO", guiFactory = "info.loenwind.enderioaddons.config.ConfigFactory")
public class EnderIOAddons
{
    public static final String MODID = "enderioaddons";
    public static final String MOD_NAME = "Ender IO Addons";
    public static final String VERSION = "@VERSION@";

    @SidedProxy(clientSide = "info.loenwind.enderioaddons.CommonProxy", serverSide = "info.loenwind.enderioaddons.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLPreInitializationEvent event) {
    	Config.init(event);
    	proxy.init(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	proxy.init(event);
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());

//        //Drain
//        if (Config.drainEnabled) {
//          ItemStack drain = new ItemStack(EnderIO.blockDrain, 1, 0);
//          GameRegistry.addRecipe(new ShapedOreRecipe(drain, "btb", "pmp", "eve", 'm', machineChassi, 't', basicTank, 'p',
//              Blocks.piston, 'b', Items.bucket, 'e', electricSteel, 'v', Items.cauldron));
//        }
    }
    
    @EventHandler
    public void init(FMLPostInitializationEvent event) throws IOException {
    	proxy.init(event);
    	System.out.println("customDiamond >> "+customDiamond.getUnlocalizedName());
    	TestX x = new TestX();
    	XStream xstream = x.makeXStream();
		test.Config cfg = new test.Config((Water) x.readConfig(xstream, "test.xml"));
		x.dump(xstream, cfg);
    }
    
    
    @ItemStackHolder(value="minecraft:diamond",meta=1,nbt="{\"my.nbttag\":1}")
    public static final ItemStack customDiamond = null;
}
