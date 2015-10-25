package info.loenwind.enderioaddons.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface INetworkUpdatable {

  @SideOnly(Side.CLIENT)
  void networkUpdate(int id, int data);

  int getNetworkUpdate(int id);

  int getNetworkUpdateCount();

  int getNetworkUpdateCheckInterval();

}
