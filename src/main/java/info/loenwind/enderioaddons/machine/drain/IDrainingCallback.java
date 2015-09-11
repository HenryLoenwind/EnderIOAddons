package info.loenwind.enderioaddons.machine.drain;

import javax.annotation.Nonnull;

import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

public interface IDrainingCallback {

  public void onWaterDrain(@Nonnull World world, @Nonnull BlockCoord bc);

  public void onWaterDrainNearby(@Nonnull World world, @Nonnull BlockCoord bc);

}
