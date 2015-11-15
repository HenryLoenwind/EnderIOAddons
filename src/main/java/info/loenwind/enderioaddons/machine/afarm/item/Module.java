package info.loenwind.enderioaddons.machine.afarm.item;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.machine.afarm.module.BestOnlyModule;
import info.loenwind.enderioaddons.machine.afarm.module.BreedModule;
import info.loenwind.enderioaddons.machine.afarm.module.CrossBreedModule;
import info.loenwind.enderioaddons.machine.afarm.module.EjectSeedsModule;
import info.loenwind.enderioaddons.machine.afarm.module.HarvestSeedsModule;
import info.loenwind.enderioaddons.machine.afarm.module.HarvestUnanalyzedModule;
import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;
import info.loenwind.enderioaddons.machine.afarm.module.MultiplyModule;
import info.loenwind.enderioaddons.machine.afarm.module.ReplaceBetterModule;
import info.loenwind.enderioaddons.machine.afarm.module.SeedAnalyzerModule;
import info.loenwind.enderioaddons.machine.afarm.module.WeedModule;

import javax.annotation.Nonnull;

public enum Module {

  BREED("BreedModule", new BreedModule()),
  CROSSBREED("CrossBreedModule", new CrossBreedModule()),
  HARVESTSEEDS("HarvestSeedsModule", new HarvestSeedsModule()),
  ANALYZESEEDS("SeedAnalyzerModule", new SeedAnalyzerModule()),
  MULTIPLY("MultiplyModule", new MultiplyModule()),
  HARVESTUNANALYZED("HarvestUnanalyzedModule", new HarvestUnanalyzedModule()),
  REPLACEBETTER("ReplaceBetterModule", new ReplaceBetterModule()),
  WEED("WeedModule", new WeedModule()),
  EJECTSEEDS("EjectSeedsModule", new EjectSeedsModule()),
  BESTONLY("BestOnlyModule", new BestOnlyModule()), ;

  @Nonnull
  public final String unlocalisedName;
  @Nonnull
  public final String iconKey;
  @Nonnull
  public final IAfarmControlModule module;

  private Module(@Nonnull String unlocalisedName, @Nonnull IAfarmControlModule module) {
    this.unlocalisedName = EnderIOAddons.DOMAIN + ".fcm" + unlocalisedName;
    this.iconKey = EnderIOAddons.DOMAIN + ":fcm" + unlocalisedName;
    this.module = module;
  }


}
