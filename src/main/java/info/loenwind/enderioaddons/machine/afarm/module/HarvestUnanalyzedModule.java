package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class HarvestUnanalyzedModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    workTile.doDestroy = workTile.agricraft.getPlantedSeed(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z) != null
        && !workTile.agricraft.isAnalyzed(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z);
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
