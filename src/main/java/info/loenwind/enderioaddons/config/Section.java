package info.loenwind.enderioaddons.config;

import javax.annotation.Nonnull;

public enum Section {
  VISUALS("visuals", false),
  RECIPES("recipes"),
  FARM("agricraft farming station"),
  FARM_NOTIF("agricraft farming station notifications", false),
  COBBLEWORKS("cobbleworks"),
  DECO("decoration block"),
  DRAIN("drain"),
  PMON("graphical power monitor"),
  IHOPPER("impulsehopper"),
  TCOM("loot recycler"),
  MAGCHARGER("magnetic charger"),
  FLAG("magnetic flag"),
  NIARD("niard"),
  VOIDTANK("void tank"),
  WATERWORKS("waterworks"),
  LIQUIDS("liquids"),
  DEV("development", false);

  @Nonnull
  public final String name;
  public final boolean sync;

  private Section(@Nonnull String name) {
    this.name = name;
    this.sync = true;
  }

  private Section(@Nonnull String name, boolean sync) {
    this.name = name;
    this.sync = sync;
  }

}
