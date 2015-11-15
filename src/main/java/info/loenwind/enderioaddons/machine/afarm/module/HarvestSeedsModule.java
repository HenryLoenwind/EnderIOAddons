package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class HarvestSeedsModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.allowHarvesting) {
      workTile.doDestroy = workTile.seed != null;
      workTile.doHarvesting = false;
    }
  }

  @Override
  public int getPriority() {
    return 11;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof HarvestSeedsModule);
  }

}
