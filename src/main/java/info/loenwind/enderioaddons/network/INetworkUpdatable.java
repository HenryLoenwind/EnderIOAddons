package info.loenwind.enderioaddons.network;

public interface INetworkUpdatable {

  void networkUpdate(int id, int data);

  int getNetworkUpdate(int id);

  int getNetworkUpdateCount();

  int getNetworkUpdateCheckInterval();

}
