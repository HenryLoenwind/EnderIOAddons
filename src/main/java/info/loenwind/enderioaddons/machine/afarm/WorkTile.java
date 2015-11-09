package info.loenwind.enderioaddons.machine.afarm;

import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.enderio.core.common.util.BlockCoord;

public class WorkTile {

  public final BlockCoord bc;
  public final TileAfarm farm;
  public final APIv1 agricraft;

  public WorkTile(BlockCoord bc, TileAfarm farm, APIv1 agricraft) {
    this.bc = bc;
    this.farm = farm;
    this.agricraft = agricraft;
  }

  public boolean done = false;
  public boolean errored = false;

}
