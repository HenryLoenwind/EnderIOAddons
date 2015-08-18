package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.DrainBlockRenderer;
import info.loenwind.enderioaddons.machine.drain.DrainFluidRenderer;
import info.loenwind.enderioaddons.machine.drain.DrainItemRenderer;
import info.loenwind.enderioaddons.machine.drain.TileDrain;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy extends ServerProxy {

  @Override
  public void init(FMLPreInitializationEvent event) {
    super.init(event);
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    BlockDrain.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new DrainBlockRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileDrain.class, new DrainFluidRenderer());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockDrain.blockDrain), new DrainItemRenderer());
  }

  @Override
  public void init(FMLPostInitializationEvent event) {
    super.init(event);
  }

}
