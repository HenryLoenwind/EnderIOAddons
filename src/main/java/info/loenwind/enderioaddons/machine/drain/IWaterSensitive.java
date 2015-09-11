package info.loenwind.enderioaddons.machine.drain;

import javax.annotation.Nonnull;

import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

public interface IWaterSensitive {

  public boolean preventInfiniteWaterForming(@Nonnull World world, @Nonnull BlockCoord bc);
  
}
