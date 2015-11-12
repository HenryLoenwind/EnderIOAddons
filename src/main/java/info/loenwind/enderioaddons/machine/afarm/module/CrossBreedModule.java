package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class CrossBreedModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if ((workTile.bc.x & 1) == 1) {
      if ((workTile.bc.z & 1) == 1) {
        workTile.seedSlot = 0;
        workTile.allowHarvesting = false;
      } else {
        workTile.seedSlot = -1;
        workTile.allowPlanting = false;
        workTile.allowCrossCrops = true;
      }
    } else {
      if ((workTile.bc.z & 1) == 1) {
        workTile.seedSlot = -1;
        workTile.allowPlanting = false;
        workTile.allowCrossCrops = true;
      } else {
        workTile.seedSlot = 2;
        workTile.allowHarvesting = false;
      }
    }
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof BreedModule) && !(other instanceof CrossBreedModule);
  }

}
