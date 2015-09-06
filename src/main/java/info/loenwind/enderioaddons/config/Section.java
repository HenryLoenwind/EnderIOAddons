package info.loenwind.enderioaddons.config;

public enum Section {
  DRAIN("drain"), COBBLEWORKS("cobbleworks");

  public final String name;
  public final boolean sync;

  private Section(String name) {
    this.name = name;
    this.sync = true;
  }

  private Section(String name, boolean sync) {
    this.name = name;
    this.sync = sync;
  }

}
