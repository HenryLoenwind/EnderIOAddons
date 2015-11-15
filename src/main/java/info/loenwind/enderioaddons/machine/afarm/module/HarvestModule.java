package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class HarvestModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.allowHarvesting) {
      workTile.doHarvesting = workTile.isMature;
    }
  }

  @Override
  public int getPriority() {
    return 10;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof HarvestModule);
  }

}
