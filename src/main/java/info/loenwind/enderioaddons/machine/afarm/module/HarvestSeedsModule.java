package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class HarvestSeedsModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    if (workTile.allowHarvesting) {
      workTile.doHarvesting = workTile.agricraft.getPlantedSeed(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z) != null;
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
