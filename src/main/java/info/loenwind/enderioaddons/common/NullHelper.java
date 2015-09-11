package info.loenwind.enderioaddons.common;

import javax.annotation.Nonnull;

public class NullHelper {

  private NullHelper() {
  }

  @Nonnull
  public static <P> P notnull(P o, @Nonnull String message) {
    if (o == null) {
      throw new NullPointerException(message);
    }
    return o;
  }

}
