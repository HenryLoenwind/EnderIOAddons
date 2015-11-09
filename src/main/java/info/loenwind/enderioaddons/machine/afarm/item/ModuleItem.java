package info.loenwind.enderioaddons.machine.afarm.item;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.machine.afarm.module.BreedModule;
import info.loenwind.enderioaddons.machine.afarm.module.CrossBreedModule;
import info.loenwind.enderioaddons.machine.afarm.module.HarvestSeedsModule;
import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;
import info.loenwind.enderioaddons.machine.afarm.module.SeedAnalyzerModule;

import javax.annotation.Nonnull;

public enum ModuleItem {

  BREED("BreedModule", new BreedModule()),
  CROSSBREED("CrossBreedModule", new CrossBreedModule()),
  HARVESTSEEDS("HarvestSeedsModule", new HarvestSeedsModule()),
  ANALYZESEEDS("SeedAnalyzerModule", new SeedAnalyzerModule()), ;

  @Nonnull
  public final String unlocalisedName;
  @Nonnull
  public final String iconKey;
  @Nonnull
  public final IAfarmControlModule module;

  private ModuleItem(@Nonnull String unlocalisedName, @Nonnull IAfarmControlModule module) {
    this.unlocalisedName = EnderIOAddons.DOMAIN + ".fcm" + unlocalisedName;
    this.iconKey = EnderIOAddons.DOMAIN + ":fcm" + unlocalisedName;
    this.module = module;
  }


}
