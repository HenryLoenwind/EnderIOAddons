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

  /**
   * stop processing after the module that sets this
   */
  public boolean done = false;
  /**
   * use seeds from this ghost slot (or any seed if slot is empty)
   */
  public int seedSlot = 0;
  /**
   * use seeds from this storage slot (must be set to a valid slot if soPlanting
   * is true)
   */
  public int seedStorageSlot = -1;
  /**
   * allow later modules to order this plot to be planted
   */
  public boolean allowPlanting = true;
  /**
   * allow later modules to order this plot to be harvested
   */
  public boolean allowHarvesting = true;
  /**
   * allow later modules to order crosscrops to be placed here
   */
  public boolean allowCrossCrops = false;
  /**
   * plant a seed from seedStorageSlot into this plot (and put down crops for
   * it)
   */
  public boolean doPlanting = false;
  /**
   * harvest this plot
   */
  public boolean doHarvesting = false;
  /**
   * put cross crops in this plot
   */
  public boolean doCrossCrops = false;

}
