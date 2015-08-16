package info.loenwind.enderioaddons.drain;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.world.World;

public interface IDrainingCallback {

  public void onWaterDrain(World world, BlockCoord bc);

  public void onWaterDrainNearby(World world, BlockCoord bc);

}
