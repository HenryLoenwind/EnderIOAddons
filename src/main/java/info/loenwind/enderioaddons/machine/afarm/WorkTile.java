package info.loenwind.enderioaddons.machine.afarm;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.enderio.core.common.util.BlockCoord;

public class WorkTile {

  public final BlockCoord bc;
  public final TileAfarm farm;
  public final APIv1 agricraft;
  public final EntityPlayerMP farmerJoe;
  public final boolean isWeeds;
  public final boolean isAnalyzed;
  public final ISeedStats stats;
  public final ItemStack seed;
  public final boolean isEmpty;
  public final boolean isMature;
  public final boolean isCrops;
  public final boolean isCrossCrops;

  public WorkTile(BlockCoord bc, TileAfarm farm, APIv1 agricraft, EntityPlayerMP farmerJoe) {
    this.bc = bc;
    this.farm = farm;
    this.agricraft = agricraft;
    this.farmerJoe = farmerJoe;

    isWeeds = agricraft.isWeeds(farm.getWorldObj(), bc.x, bc.y, bc.z);
    isAnalyzed = agricraft.isAnalyzed(farm.getWorldObj(), bc.x, bc.y, bc.z);
    stats = agricraft.getStats(farm.getWorldObj(), bc.x, bc.y, bc.z);
    seed = agricraft.getPlantedSeed(farm.getWorldObj(), bc.x, bc.y, bc.z);
    isEmpty = agricraft.isEmpty(farm.getWorldObj(), bc.x, bc.y, bc.z);
    isMature = agricraft.isMature(farm.getWorldObj(), bc.x, bc.y, bc.z);
    isCrops = agricraft.isCrops(farm.getWorldObj(), bc.x, bc.y, bc.z);
    isCrossCrops = agricraft.isCrossCrops(farm.getWorldObj(), bc.x, bc.y, bc.z);
  }

  /**
   * keep doing something to this tile
   */
  public boolean doneSomething = false;
  /**
   * use seeds from this ghost slot (or any seed if slot is empty)
   */
  public int seedSlot = 0;
  /**
   * use seeds from this storage slot (must be set to a valid slot if doPlanting
   * is true)
   */
  public int seedStorageSlot = -1;
  /**
   * use crop sticks from this storage slot (must be set to a valid slot if
   * doCrops is true)
   */
  public int cropsSlot = -1;
  /**
   * use fertilizer from this storage slot (must be set to a valid slot if
   * doFertilize is true)
   */
  public int fertilizerSlot = -1;
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
   * harvest this plot including seeds
   */
  public boolean doDestroy = false;
  /**
   * put cross crops in this plot
   */
  public boolean doCrossCrops = false;
  /**
   * put crops in this plot
   */
  public boolean doCrops = false;
  /**
   * till this plot
   */
  public boolean doTill = false;
  /**
   * weed this plot
   */
  public boolean doWeed = false;
  /**
   * fertilize this plot
   */
  public boolean doFertilize = false;

  @Override
  public String toString() {
    return "WorkTile [bc=" + bc + ", isWeeds=" + isWeeds + ", isAnalyzed=" + isAnalyzed + ", seed=" + seed + ", isEmpty=" + isEmpty
        + ", isMature=" + isMature + ", isCrops=" + isCrops + ", isCrossCrops=" + isCrossCrops + ", seedSlot=" + seedSlot + ", seedStorageSlot="
        + seedStorageSlot + ", cropsSlot=" + cropsSlot + ", fertilizerSlot=" + fertilizerSlot + ", allowPlanting=" + allowPlanting + ", allowHarvesting="
        + allowHarvesting + ", allowCrossCrops=" + allowCrossCrops + ", doPlanting=" + doPlanting + ", doHarvesting=" + doHarvesting + ", doDestroy="
        + doDestroy + ", doCrossCrops=" + doCrossCrops + ", doCrops=" + doCrops + ", doTill=" + doTill + ", doWeed=" + doWeed + ", doFertilize=" + doFertilize
        + "]";
  }

}
