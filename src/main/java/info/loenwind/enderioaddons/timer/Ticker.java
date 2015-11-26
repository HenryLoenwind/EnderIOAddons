package info.loenwind.enderioaddons.timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class Ticker {

  public static final Ticker instance = new Ticker();

  private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

  private Ticker() {
  }

  public static void create() {
    FMLCommonHandler.instance().bus().register(instance);
  }

  public static void enqueue(Runnable task) {
    instance.queue.add(task);
  }

  @SubscribeEvent
  public void onTick(ServerTickEvent evt) {
    if (evt.phase == Phase.END && !queue.isEmpty()) {
      Runnable task = queue.poll();
      if (task != null) {
        task.run();
      }
    }
  }

}