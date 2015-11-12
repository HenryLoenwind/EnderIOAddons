package info.loenwind.enderioaddons.machine.afarm.module;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

public class MultiplyModule implements IAfarmControlModule {

  private static final ForgeDirection[] AROUND = { NORTH, SOUTH, EAST, WEST };

  @Override
  public void doWork(WorkTile workTile) {
    for (ForgeDirection direction : AROUND) {
      BlockCoord toCheck = workTile.bc.getLocation(direction);
      if (workTile.agricraft.isCrops(workTile.farm.getWorldObj(), toCheck.x, toCheck.y, toCheck.z)) {
        if (workTile.agricraft.isEmpty(workTile.farm.getWorldObj(), toCheck.x, toCheck.y, toCheck.z)
            || workTile.agricraft.isWeeds(workTile.farm.getWorldObj(), toCheck.x, toCheck.y, toCheck.z)
            || workTile.agricraft.isCrossCrops(workTile.farm.getWorldObj(), toCheck.x, toCheck.y, toCheck.z)) {
          workTile.allowHarvesting = false;
        } else if (workTile.agricraft.isMature(workTile.farm.getWorldObj(), toCheck.x, toCheck.y, toCheck.z)) {
          workTile.allowCrossCrops = true;
          workTile.allowPlanting = false;
        } else {
          workTile.allowPlanting = false;
        }
      }
    }
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof MultiplyModule) && !(other instanceof BreedModule) && !(other instanceof CrossBreedModule);
  }

}
