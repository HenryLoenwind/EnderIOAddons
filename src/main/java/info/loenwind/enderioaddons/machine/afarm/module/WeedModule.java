package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

public class WeedModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    workTile.doWeed = workTile.agricraft.isWeeds(workTile.farm.getWorldObj(), workTile.bc.x, workTile.bc.y, workTile.bc.z);
  }

  @Override
  public int getPriority() {
    return 60;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof WeedModule);
  }

}
