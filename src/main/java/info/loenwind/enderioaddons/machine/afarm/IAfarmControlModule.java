package info.loenwind.enderioaddons.machine.afarm;

public interface IAfarmControlModule {

  void doWork(WorkTile workTile);

  int getPriority();

}
