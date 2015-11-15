package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.config.Config;

public enum Notif {
  NO_POWER(Config.farmNO_POWEREnabled),
  NO_HOE(Config.farmNO_HOEEnabled),
  NO_RAKE(Config.farmNO_RAKEEnabled),
  NO_CROPS(Config.farmNO_CROPSEnabled),
  NO_SEEDS(Config.farmNO_SEEDSEnabled),
  NO_FERTILIZER(Config.farmNO_FERTILIZEREnabled),
  FULL(Config.farmFULLEnabled),
  WEEDS(Config.farmWEEDSEnabled),
  OFFLINE(Config.farmOFFLINEEnabled);

  private final Config cfg;

  private Notif(Config cfg) {
    this.cfg = cfg;
  }

  public boolean show() {
    return cfg.getBoolean();
  }
}
