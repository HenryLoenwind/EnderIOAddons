package info.loenwind.enderioaddons.machine.drain;

import static info.loenwind.enderioaddons.common.NullHelper.notnullM;
import info.loenwind.waterhooks.WaterFormEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Log;

import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class InfiniteWaterSourceStopper {

  @Nonnull
  private static final InfiniteWaterSourceStopper instance = new InfiniteWaterSourceStopper();
  
  @Nonnull
  public static InfiniteWaterSourceStopper getInstance() {
    return instance;
  }
  
  public static void register() {
    MinecraftForge.EVENT_BUS.register(instance);
    for (ModContainer modContainer : ModAPIManager.INSTANCE.getAPIList()) {
      Log.info(modContainer.getModId() + " version " + modContainer.getVersion() + " from " + modContainer.getSource().getName());
    }
  }
 
  @Nonnull
  private final Map<Integer, Map<IWaterSensitive, Object>> teblMap = new HashMap<Integer, Map<IWaterSensitive, Object>>();
  
  @SubscribeEvent
  public void onWaterForming(WaterFormEvent event) {
    final World world = notnullM(event.world, "event.world");
    Map<IWaterSensitive, Object> tebl = teblMap.get(world.provider.dimensionId);
    if (tebl != null) {
      BlockCoord bc = new BlockCoord(event.x, event.y, event.z);
      for (IWaterSensitive hook : tebl.keySet()) {
        if (hook.preventInfiniteWaterForming(world, bc)) {
          event.setCanceled(true);
          return;
        }
      }
    }
  }

  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    teblMap.remove(event.world.provider.dimensionId);
  }
  
  public void unregister(@Nonnull World world, @Nonnull IWaterSensitive hook) {
    Map<IWaterSensitive, Object> tebl = teblMap.get(world.provider.dimensionId);
    if (tebl != null) {
      tebl.remove(hook);
      if (tebl.isEmpty()) {
        teblMap.remove(world.provider.dimensionId);
      }
    }
  }
  
  public void register(@Nonnull World world, @Nonnull IWaterSensitive hook) {
    Map<IWaterSensitive, Object> tebl = teblMap.get(world.provider.dimensionId);
    if (tebl == null) {
      tebl = new WeakHashMap<IWaterSensitive, Object>();
      teblMap.put(world.provider.dimensionId, tebl);
    }
    tebl.put(hook, null);
  }

}
