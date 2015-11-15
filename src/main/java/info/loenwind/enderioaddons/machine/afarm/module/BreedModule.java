package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class BreedModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if ((workTile.bc.x & 1) == (workTile.bc.z & 1)) {
      workTile.allowHarvesting = false;
    } else {
      workTile.allowPlanting = false;
      workTile.allowCrossCrops = true;
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
