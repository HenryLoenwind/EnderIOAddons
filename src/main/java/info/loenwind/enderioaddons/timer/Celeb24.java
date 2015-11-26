package info.loenwind.enderioaddons.timer;

import info.loenwind.enderioaddons.EnderIOAddons;

import java.util.Calendar;
import java.util.Locale;

public class Celeb24 implements Event {

  private final Calendar start = Calendar.getInstance(Locale.getDefault());
  private final Calendar end = Calendar.getInstance(Locale.getDefault());

  public static void create() {
    Scheduler.instance.registerEvent(new Celeb24());
  }

  private Celeb24() {
    start.set(2015, 11, 24, 12, 0, 0);
    end.set(2015, 11, 27, 0, 0, 0);
  }

  @Override
  public boolean isActive(Calendar now) {
    if (start.before(now)) {
      if (end.before(now)) {
        calculate(now);
        EnderIOAddons.mode24 = false;
        return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public long getTimeToStart(Calendar now) {
    long remaining = start.getTimeInMillis() - now.getTimeInMillis();
    return remaining < 0 ? 0 : remaining;
  }

  @Override
  public void calculate(Calendar now) {
    while (end.before(now)) {
      start.add(Calendar.YEAR, 1);
      end.add(Calendar.YEAR, 1);
    }
  }

  @Override
  public void run(Calendar now) {
    EnderIOAddons.mode24 = true;
  }

  @Override
  public String toString() {
    return "Celeb24 [start=" + start + ", end=" + end + "]";
  }

}
