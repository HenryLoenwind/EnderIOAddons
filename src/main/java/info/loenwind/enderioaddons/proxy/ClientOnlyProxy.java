package info.loenwind.enderioaddons.proxy;

import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.cobbleworks.RendererCobbleworks;
import info.loenwind.enderioaddons.machine.cobbleworks.TileCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.DrainBlockRenderer;
import info.loenwind.enderioaddons.machine.drain.DrainFluidRenderer;
import info.loenwind.enderioaddons.machine.drain.DrainItemRenderer;
import info.loenwind.enderioaddons.machine.drain.TileDrain;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;
import info.loenwind.enderioaddons.machine.framework.TESRFrameworkMachine;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.ihopper.RendererIHopper;
import info.loenwind.enderioaddons.machine.ihopper.TileIHopper;
import info.loenwind.enderioaddons.machine.niard.BlockNiard;
import info.loenwind.enderioaddons.machine.niard.BlockRendererNiard;
import info.loenwind.enderioaddons.machine.niard.FluidRendererNiard;
import info.loenwind.enderioaddons.machine.niard.ItemRendererNiard;
import info.loenwind.enderioaddons.machine.niard.TileNiard;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePartRenderer;
import info.loenwind.enderioaddons.machine.waterworks.BlockWaterworks;
import info.loenwind.enderioaddons.machine.waterworks.RendererWaterworks;
import info.loenwind.enderioaddons.machine.waterworks.TileWaterworks;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientOnlyProxy extends ClientAndServerProxy {

  @Override
  public void init(FMLPreInitializationEvent event) {
    super.init(event);
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);

    BlockDrain.blockDrain.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new DrainBlockRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileDrain.class, new DrainFluidRenderer());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockDrain.blockDrain), new DrainItemRenderer());

    BlockNiard.blockNiard.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new BlockRendererNiard());
    ClientRegistry.bindTileEntitySpecialRenderer(TileNiard.class, new FluidRendererNiard());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockNiard.blockNiard), new ItemRendererNiard());

    RendererFrameworkMachine rendererFrameworkMachine = new RendererFrameworkMachine();

    BlockCobbleworks.blockCobbleworks.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new RendererCobbleworks(rendererFrameworkMachine));
    ClientRegistry.bindTileEntitySpecialRenderer(TileCobbleworks.class, new TESRFrameworkMachine());

    BlockWaterworks.blockWaterworks.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new RendererWaterworks(rendererFrameworkMachine));
    ClientRegistry.bindTileEntitySpecialRenderer(TileWaterworks.class, new TESRFrameworkMachine());

    BlockIHopper.blockIHopper.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new RendererIHopper(rendererFrameworkMachine));
    ClientRegistry.bindTileEntitySpecialRenderer(TileIHopper.class, new TESRFrameworkMachine());

    MinecraftForgeClient.registerItemRenderer(ItemMachinePart.itemMachinePart, new MachinePartRenderer(rendererFrameworkMachine));

  }

  @Override
  public void init(FMLPostInitializationEvent event) {
    super.init(event);
  }

}
