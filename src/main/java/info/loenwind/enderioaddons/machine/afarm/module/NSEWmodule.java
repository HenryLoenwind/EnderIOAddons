package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class NSEWmodule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.bc.x <= workTile.farm.xCoord && workTile.bc.z < workTile.farm.zCoord) {
      workTile.seedSlot = 0;
    } else if (workTile.bc.x < workTile.farm.xCoord && workTile.bc.z >= workTile.farm.zCoord) {
      workTile.seedSlot = 3;
    } else if (workTile.bc.x > workTile.farm.xCoord && workTile.bc.z <= workTile.farm.zCoord) {
      workTile.seedSlot = 1;
    } else if (workTile.bc.x >= workTile.farm.xCoord && workTile.bc.z > workTile.farm.zCoord) {
      workTile.seedSlot = 2;
    } else {
      workTile.seedSlot = -1;
    }
  }

  @SuppressWarnings("static-method")
  public void doWork_old(WorkTile workTile) {
    if (workTile.bc.x < workTile.farm.xCoord) {
      if (workTile.bc.z < workTile.farm.zCoord) {
        workTile.seedSlot = 0;
      } else {
        workTile.seedSlot = 3;
      }
    } else {
      if (workTile.bc.z < workTile.farm.zCoord) {
        workTile.seedSlot = 1;
      } else {
        workTile.seedSlot = 2;
      }
    }
  }

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof NSEWmodule);
  }

}
