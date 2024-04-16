import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Simple doubly-linked lists.
 */
public class SimpleCDLL<T> implements SimpleList<T> {
  // +--------+------------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * A dummy node at the front (and back) of the list
   */
  Node2<T> dummy;

  /**
   * The number of values in the list.
   */
  int size;

  /**
   * The number of updates to the list.
   */
  int updates;

  // +--------------+------------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create an empty list.
   */
  public SimpleCDLL() {
    this.dummy = new Node2<T>(null);
    this.dummy.prev = dummy;
    this.dummy.next = dummy;
    this.size = 0;
    this.updates = 0;
  } // SimpleCDLL

  // +-----------+---------------------------------------------------------
  // | Iterators |
  // +-----------+

  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      // +--------+--------------------------------------------------------
      // | Fields |
      // +--------+

      /**
       * The position in the list of the next value to be returned.
       * Included because ListIterators must provide nextIndex and
       * prevIndex.
       */
      int pos = 0;

      /**
       * The cursor is between neighboring values, so we start links
       * to the previous and next value..
       */
      Node2<T> prev = SimpleCDLL.this.dummy;
      Node2<T> next = prev.next;

      /**
       * The node to be updated by remove or set.  Has a value of
       * null when there is no such value.
       */
      Node2<T> update = null;

      /**
       * The number of updates at the time this was created (or last updated).
       */
      int updates = SimpleCDLL.this.updates;

      // +----------------+----------------------------------------------
      // | Public methods |
      // +----------------+

      public void add(T val) throws UnsupportedOperationException {
        this.failFast();

        // Insert the new value
        this.prev = this.prev.insertAfter(val);

        // Note that we cannot update
        this.update = null;

        // Increase the size
        ++SimpleCDLL.this.size;

        // Update the position.  (See SimpleArrayList.java for more of
        // an explanation.)
        ++this.pos;

        // Note that we've updated the list
        this.noteChange();
      } // add(T)

      public boolean hasNext() {
        this.failFast();
        return (this.pos < SimpleCDLL.this.size);
        // We could also use this.next != SimpleCDLL.this.dummy
      } // hasNext()

      public boolean hasPrevious() {
        this.failFast();
        return (this.pos > 0);
        // We could also use this.next != SimpleCDLL.this.dummy
      } // hasPrevious()

      public T next() {
        this.failFast();
        if (!this.hasNext()) {
         throw new NoSuchElementException();
        } // if
        // Identify the node to update
        this.update = this.next;
        // Advance the cursor
        this.prev = this.next;
        this.next = this.next.next;
        // Note the movement
        ++this.pos;
        // And return the value
        return this.update.value;
      } // next()

      public int nextIndex() {
        this.failFast();
        return this.pos;
      } // nextIndex()

      public int previousIndex() {
        this.failFast();
        return this.pos - 1;
      } // prevIndex

      public T previous() throws NoSuchElementException {
        this.failFast();
        if (!this.hasPrevious()) {
          throw new NoSuchElementException();
        }
        // Identify the node to update
        this.update = this.prev;
        // Move the cursor
        this.prev = this.prev.prev;
        this.next = this.prev.next;
        // Note the movement
        --this.pos;
        // And return the value
        return this.update.value;
      } // previous()

      public void remove() {
        this.failFast();

        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Update the cursor
        if (this.next == this.update) {
          this.next = this.update.next;
        } // if
        if (this.prev == this.update) {
          this.prev = this.update.prev;
          --this.pos;
        } // if

        // Do the real work
        this.update.remove();
        --SimpleCDLL.this.size;

        // Note that no more updates are possible
        this.update = null;

        // Note that we've changed the state.
        this.noteChange();
      } // remove()

      public void set(T val) {
        this.failFast();

        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Do the real work
        this.update.value = val;

        // Note that no more updates are possible
        this.update = null;
      } // set(T)

      // +-----------------+---------------------------------------------
      // | Private methods |
      // +-----------------+

      /**
       * Determine if we are out of sync with the list. If so, throw an IllegalStateException.
       */
      void failFast() {
        if (this.updates != SimpleCDLL.this.updates) {
          throw new ConcurrentModificationException("The list has been updated since this iterator was created.");
        } // if updates don't match
      } // failFast()

      /**
       * Note that the list has changed. Should only be called after failFast.
       */
      void noteChange() {
        ++SimpleCDLL.this.updates;
        ++this.updates;
      } // noteChange

    };
  } // listIterator()

} // class SimpleCDLL<T>
