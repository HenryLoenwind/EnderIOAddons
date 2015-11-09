package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.WorkTile;

/**
 * Modules:
 * 
 * <ul>
 * <li>SeedAnalyzer(-1) - analyze seeds in storage</li>
 * <li>NSEW(S:0) - select slots according to cardinal directions</li>
 * <li>Breed(1) - Checker pattern of planting and harvesting, no change to slots
 * </li>
 * <li>CrossBreed(1) - Checker pattern of planting and harvesting, with planting
 * in stripes of slot 0 and 3</li>
 * <li>Harvest(S:10) - for plots that are allowed to be harvested, set to be
 * harvested if plants are mature</li>
 * <li>HarvestSeeds(11) - for plots that are allowed to be harvested, set to be
 * harvested if there is plant there (overwrites Harvest module)</li>
 * </ul>
 *
 * Modules TODO:
 *
 * <ul>
 * <li>ReplaceBetter(30) - force plant to be harvested if there's a better seed
 * in storage (extends SeedAnalyzer)</li>
 * <li>HarvestUnanalyzed(30) - force plant to be harvested if it is not analyzed
 * </li>
 * <li>Plant(S:40) - checks if a seed can be planted and set the doPlanting
 * flag; selects seed storage slot</li>
 * <li>CrossCrop(S:50) - checks if cross crops are allowed and possible here and
 * set the doCrossCrops flag accordingly</li>
 * <li>Weed(60) - remove weeds</li>
 * <li>Execute(S:100) - execute harvesting, planting and crosscroping according
 * to the do* flags. Sets done if it does nothing</li>
 * <li>Best only(110) - move seeds from storage to output if there's a better
 * one in storage (needs a new working hook) (extends SeedAnalyzer)</li>
 * <li>EjectSeeds(110) - move all seeds from storage to output (extends
 * SeedAnalyzer)</li>
 * </ul>
 */
public interface IAfarmControlModule {

  void doWork(WorkTile workTile);

  int getPriority();

  boolean isCompatibleWith(IAfarmControlModule other);
}
