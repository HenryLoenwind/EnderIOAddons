package info.loenwind.enderioaddons.machine.afarm;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class NotifSet extends AbstractSet<Notif> {
  private long elements = 0L;
  private long lastseen = 0L;

  public NotifSet() {
    super();
  }

  public long getElements() {
    lastseen = elements;
    return elements;
  }

  public void setElements(long elements) {
    this.elements = elements;
  }

  public boolean isChanged() {
    return lastseen != elements;
  }

  @Override
  public Iterator<Notif> iterator() {
    return new NotifSetIterator();
  }

  @Override
  public int size() {
    return Long.bitCount(this.elements);
  }

  @Override
  public boolean isEmpty() {
    return (this.elements == 0L);
  }

  @Override
  public boolean contains(Object paramObject) {
    if (!(paramObject instanceof Notif))
      return false;
    return ((this.elements & 1L << ((Notif) paramObject).ordinal()) != 0L);
  }

  @Override
  public boolean add(Notif paramE) {
    long l = this.elements;
    this.elements |= 1L << paramE.ordinal();
    return (this.elements != l);
  }

  @Override
  public boolean remove(Object paramObject) {
    if (!(paramObject instanceof Notif))
      return false;
    long l = this.elements;
    this.elements &= (1L << ((Notif) paramObject).ordinal() ^ 0xFFFFFFFF);
    return (this.elements != l);
  }

  @Override
  public void clear() {
    this.elements = 0L;
  }

  private class NotifSetIterator implements Iterator<Notif> {
    long unseen = NotifSet.this.elements;
    long lastReturned = 0L;

    @Override
    public boolean hasNext() {
      return (this.unseen != 0L);
    }

    @Override
    public Notif next() {
      if (this.unseen == 0L)
        throw new NoSuchElementException();
      this.lastReturned = (this.unseen & -this.unseen);
      this.unseen -= this.lastReturned;
      return Notif.values()[Long.numberOfTrailingZeros(this.lastReturned)];
    }

    @Override
    public void remove() {
    }
  }
}
