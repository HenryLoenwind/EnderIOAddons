package info.loenwind.enderioaddons.proxy;

import info.loenwind.enderioaddons.machine.afarm.AgriDetector;
import info.loenwind.enderioaddons.machine.afarm.BlockAfarm;
import info.loenwind.enderioaddons.machine.afarm.ItemRendererAfarm;
import info.loenwind.enderioaddons.machine.afarm.RendererAfarm;
import info.loenwind.enderioaddons.machine.cobbleworks.BlockCobbleworks;
import info.loenwind.enderioaddons.machine.cobbleworks.RendererCobbleworks;
import info.loenwind.enderioaddons.machine.cobbleworks.TileCobbleworks;
import info.loenwind.enderioaddons.machine.drain.BlockDrain;
import info.loenwind.enderioaddons.machine.drain.DrainBlockRenderer;
import info.loenwind.enderioaddons.machine.drain.DrainFluidRenderer;
import info.loenwind.enderioaddons.machine.drain.DrainItemRenderer;
import info.loenwind.enderioaddons.machine.drain.TileDrain;
import info.loenwind.enderioaddons.machine.flag.BlockFlag;
import info.loenwind.enderioaddons.machine.flag.ItemRendererFlag;
import info.loenwind.enderioaddons.machine.flag.TESRFlag;
import info.loenwind.enderioaddons.machine.flag.TileFlag;
import info.loenwind.enderioaddons.machine.framework.RendererFrameworkMachine;
import info.loenwind.enderioaddons.machine.framework.TESRFrameworkMachine;
import info.loenwind.enderioaddons.machine.ihopper.BlockIHopper;
import info.loenwind.enderioaddons.machine.ihopper.RendererIHopper;
import info.loenwind.enderioaddons.machine.ihopper.TileIHopper;
import info.loenwind.enderioaddons.machine.magcharger.BlockMagCharger;
import info.loenwind.enderioaddons.machine.magcharger.ItemRendererMagCharger;
import info.loenwind.enderioaddons.machine.magcharger.RendererMagCharger;
import info.loenwind.enderioaddons.machine.magcharger.TESRMagCharger;
import info.loenwind.enderioaddons.machine.magcharger.TileMagCharger;
import info.loenwind.enderioaddons.machine.niard.BlockNiard;
import info.loenwind.enderioaddons.machine.niard.BlockRendererNiard;
import info.loenwind.enderioaddons.machine.niard.FluidRendererNiard;
import info.loenwind.enderioaddons.machine.niard.ItemRendererNiard;
import info.loenwind.enderioaddons.machine.niard.TileNiard;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePartRenderer;
import info.loenwind.enderioaddons.machine.pmon.BlockPMon;
import info.loenwind.enderioaddons.machine.pmon.ItemRendererPMon;
import info.loenwind.enderioaddons.machine.pmon.RendererPMon;
import info.loenwind.enderioaddons.machine.pmon.TESRPMon;
import info.loenwind.enderioaddons.machine.pmon.TilePMon;
import info.loenwind.enderioaddons.machine.tcom.BlockTcom;
import info.loenwind.enderioaddons.machine.tcom.RendererTcom;
import info.loenwind.enderioaddons.machine.tcom.TESRTcom;
import info.loenwind.enderioaddons.machine.tcom.TileTcom;
import info.loenwind.enderioaddons.machine.voidtank.BlockVoidTank;
import info.loenwind.enderioaddons.machine.voidtank.FluidRendererVoidTank;
import info.loenwind.enderioaddons.machine.voidtank.ItemRendererVoidTank;
import info.loenwind.enderioaddons.machine.voidtank.RendererVoidTank;
import info.loenwind.enderioaddons.machine.voidtank.TileVoidTank;
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

    BlockVoidTank.blockVoidTank.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new RendererVoidTank());
    ClientRegistry.bindTileEntitySpecialRenderer(TileVoidTank.class, new FluidRendererVoidTank());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockVoidTank.blockVoidTank), new ItemRendererVoidTank());

    BlockPMon.blockPMon.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new RendererPMon());
    ClientRegistry.bindTileEntitySpecialRenderer(TilePMon.class, new TESRPMon());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockPMon.blockPMon), new ItemRendererPMon());

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

    BlockTcom.blockTcom.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    final RendererTcom rendererTcom = new RendererTcom(rendererFrameworkMachine);
    RenderingRegistry.registerBlockHandler(rendererTcom);
    ClientRegistry.bindTileEntitySpecialRenderer(TileTcom.class, new TESRTcom(rendererTcom));

    MinecraftForgeClient.registerItemRenderer(ItemMachinePart.itemMachinePart, new MachinePartRenderer(rendererFrameworkMachine));

    ClientRegistry.bindTileEntitySpecialRenderer(TileFlag.class, new TESRFlag());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockFlag.blockFlag), new ItemRendererFlag());

    BlockMagCharger.blockMagCharger.localRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new RendererMagCharger());
    ClientRegistry.bindTileEntitySpecialRenderer(TileMagCharger.class, new TESRMagCharger());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockMagCharger.blockMagCharger), new ItemRendererMagCharger());

    if (AgriDetector.hasAgri) {
      BlockAfarm.blockAfarm.localRenderId = RenderingRegistry.getNextAvailableRenderId();
      RenderingRegistry.registerBlockHandler(new RendererAfarm());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockAfarm.blockAfarm), new ItemRendererAfarm());
    }

  }

  @Override
  public void init(FMLPostInitializationEvent event) {
    super.init(event);
  }

}
