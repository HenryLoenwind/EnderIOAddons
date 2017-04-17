package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class HarvestUnanalyzedModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    workTile.doDestroy |= workTile.seed != null && !workTile.isAnalyzed;
  }

  @Override
  public int getPriority() {
    return 30;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof HarvestUnanalyzedModule);
  }

}
