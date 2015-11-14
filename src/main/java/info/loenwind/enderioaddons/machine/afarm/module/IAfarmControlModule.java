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
 * <li>Multiply(1) - fill the field with plants from as few seeds as possible by
 * placing seeds and crosscrops as best for spreading, no change to slots</li>
 * <li>Harvest(S:10) - for plots that are allowed to be harvested, set to be
 * harvested if plants are mature</li>
 * <li>HarvestSeeds(11) - for plots that are allowed to be harvested, set to be
 * harvested if there is plant there (overwrites Harvest module)</li>
 * <li>HarvestUnanalyzed(30) - force plant to be harvested if it is not analyzed
 * </li>
 * <li>ReplaceBetter(30) - force plant to be harvested if there's a better seed
 * in storage (ignores unanalyzed plants)</li>
 * <li>Plant(S:40) - checks if a seed can be planted and set the doPlanting
 * flag; selects seed storage slot; orders tilling if needed</li>
 * <li>CrossCrop(S:50) - checks if cross crops are allowed and possible here and
 * set the doCrossCrops flag accordingly</li>
 * <li>Crop(S:51) - checks if crops are allowed and possible here and set the
 * doCrops flag accordingly; also selects the crops slot</li>
 * <li>Weed(60) - check if there are weeds and set weeding flag</li>
 * <li>Execute*(S:100) - execute harvesting, destroying, planting, tilling,
 * cropping, weeding and crosscroping according to the do* flags (multiple
 * modules)</li>
 * <li>EjectSeeds(110) - move all seeds from storage to output</li>
 * <li>Best only(110) - move seeds from storage to output if there's a better
 * one in storage</li>
 * </ul>
 *
 */
public interface IAfarmControlModule {

  void doWork(WorkTile workTile);

  int getPriority();

  boolean isCompatibleWith(IAfarmControlModule other);
}
