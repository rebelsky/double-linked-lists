import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Tests of the SimpleCDLL class.
 *
 * @author Samuel A. Rebelsky
 */
public class SimpleCDLLTests {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * An array of strings for our experiments.
   */
  SimpleCDLL<String> strings;

  // +-----------+---------------------------------------------------
  // | Utilities |
  // +-----------+

  /**
   * Set up the array of strings with "A", "B", "C", "D", "E".
   */
  @BeforeEach
  void setupStrings() {
    strings = new SimpleCDLL<String>();
    ListIterator<String> lit = strings.listIterator();
    lit.add("A");
    lit.add("B");
    lit.add("C");
    lit.add("D");
    lit.add("E");
  } // setupStrings()

  <T> int length(SimpleCDLL<T> lst) {
    int len = 0;
    for (T val : lst) {
      ++len;
    }
    return len;
  } // length(SimpleCDLL<T>)

  <T> String[] toStringArray(SimpleCDLL<T> lst) {
    String[] result = new String[length(lst)];
    int pos = 0;
    for (T val : lst) {
      result[pos++] = val.toString();
    } // for
    return result;
  } // toStringArray(SimpleCDLL<T>)

  // +---------------+-----------------------------------------------
  // | R-level tests |
  // +---------------+

  // Hmmm ... There may be no R-level tests.

  // +---------------+-----------------------------------------------
  // | M-level tests |
  // +---------------+

  @Test
  void test1M_01_setup() {
    assertArrayEquals(new String[] { "A", "B", "C", "D", "E" },
        toStringArray(strings),
        "initial setup");
  } // test1M_01

  @Test
  void test1M_02_removeAll() {
    ListIterator<String> lit = strings.listIterator();
    while (lit.hasNext()) {
      lit.next();
      lit.remove();
    } // while
    assertArrayEquals(new String[] {},
        toStringArray(strings),
        "remove all");
  } // test1M_02

  @Test
  void test1M_03_removeAllFromBack() {
    ListIterator<String> lit = strings.listIterator();
    while (lit.hasNext()) {
      lit.next();
    } // while
    while (lit.hasPrevious()) {
      lit.previous();
      lit.remove();
    } // while
    assertArrayEquals(new String[] {},
        toStringArray(strings),
        "remove all from back");
  } // test1M_03

  @Test
  void test1M_04_removeWithoutNext() {
    ListIterator<String> lit = strings.listIterator();
    assertThrows(IllegalStateException.class,
        () -> {
          lit.remove();
        },
        "remove before calling next");
  } // test1M_04

  @Test
  void test1M_05_removeFirst() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.remove();
    assertArrayEquals(new String[] { "B", "C", "D", "E" },
        toStringArray(strings),
        "remove first element");
  } // test1M_05

  @Test
  void test1M_06_removeSecond() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.next();
    lit.remove();
    assertArrayEquals(new String[] { "A", "C", "D", "E" },
        toStringArray(strings),
        "remove second element");
  } // test1M_06

  @Test
  void test1M_07_removeSecondBackward() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.next();
    lit.previous();
    lit.remove();
    assertArrayEquals(new String[] { "A", "C", "D", "E" },
        toStringArray(strings),
        "remove second element");
  } // test1M_07

  @Test
  void test1M_08_removeSecondTwice() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.next();
    lit.remove();
    assertThrows(IllegalStateException.class,
        () -> lit.remove(),
        "remove twice in a row");
  } // test1M_08

  @Test
  void test1M_09_removeThenInsert() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.next();
    lit.remove();
    lit.add("X");
    lit.add("Y");
    assertArrayEquals(new String[] { "A", "X", "Y", "C", "D", "E" },
        toStringArray(strings),
        "remove then insert");
  } // test1M_09

  @Test
  void test1M_10_prevThenInsert() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.next();
    lit.previous();
    lit.add("X");
    lit.add("Y");
    assertArrayEquals(new String[] { "A", "X", "Y", "B", "C", "D", "E" },
        toStringArray(strings),
        "previous then insert");
  } // test1M_10

  @Test
  void test1M_11_prevAtFront() {
    ListIterator<String> lit = strings.listIterator();
    assertThrows(NoSuchElementException.class,
        () -> lit.previous(),
        "previous at front");
  } // test1M_11

  @Test
  void test1M_12_nextAtEnd() {
    ListIterator<String> lit = strings.listIterator();
    while (lit.hasNext()) {
      lit.next();
    }
    assertThrows(NoSuchElementException.class,
        () -> lit.next(),
        "next at end");
  } // test1M_12

  @Test
  void test1M_13_insertAtEnd() {
    ListIterator<String> lit = strings.listIterator();
    while (lit.hasNext()) {
      lit.next();
    }
    lit.add("F");
    lit.add("G");
    assertArrayEquals(new String[] { "A", "B", "C", "D", "E", "F", "G" },
        toStringArray(strings),
        "add at end");
  } // test1M_13

  @Test
  void test1M_14_insertThenRemove() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.add("X");
    assertThrows(IllegalStateException.class,
        () -> lit.remove(),
        "remove after insert");
  } // test1M_14

  @Test
  void test1M_15_insertThenNextThenRemove() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.add("X");
    lit.next();
    lit.remove();
    assertArrayEquals(new String[] { "A", "X", "C", "D", "E" },
        toStringArray(strings),
        "add next remove");
  } // test1M_15

  @Test
  void test1M_16_insertThenPrevThenRemove() {
    ListIterator<String> lit = strings.listIterator();
    lit.next();
    lit.add("X");
    lit.previous();
    lit.remove();
    assertArrayEquals(new String[] { "A", "B", "C", "D", "E" },
        toStringArray(strings),
        "add prev remove");
  } // test1M_16

  @Test
  void test1M_17_replaceAll() {
    ListIterator<String> lit = strings.listIterator();
    lit.add("F");
    lit.next();
    lit.remove();
    lit.add("G");
    lit.next();
    lit.remove();
    lit.add("H");
    lit.next();
    lit.remove();
    lit.add("I");
    lit.next();
    lit.remove();
    lit.add("J");
    lit.next();
    lit.remove();
    assertArrayEquals(new String[] { "F", "G", "H", "I", "J" },
        toStringArray(strings),
        "replace all, one by one");
  } // test1M_17

  @Test
  void test1M_18_alternateIteration() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    assertEquals("A", lit1.next(), "A at front, lit 1");
    assertEquals("B", lit1.next(), "B next element, lit 1");
    assertEquals("A", lit2.next(), "A at front, lit 2");
    assertEquals("C", lit1.next(), "C third element lit 1");
  } // test1M_18

  @Test
  void test1M_19_alternateIterationThenInvalidate() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    assertEquals("A", lit1.next(), "A at front, lit 1");
    assertEquals("B", lit1.next(), "B next element, lit 1");
    assertEquals("A", lit2.next(), "A at front, lit 2");
    lit2.remove();
    assertThrows(ConcurrentModificationException.class,
        () -> lit1.next(),
        "lit1 throws eception after lit2 modifies list");
  } // test1M_19

  @Test
  void test1M_20_alternateIterationAfterModification() {
    ListIterator<String> lit1 = strings.listIterator();
    lit1.next();
    lit1.remove();
    ListIterator<String> lit2 = strings.listIterator();
    assertEquals("B", lit1.next(), "lit1 new first element");
    assertEquals("C", lit1.next(), "lit1 new second element");
    assertEquals("B", lit2.next(), "lit2 new first element");
  } // test1M_20

  @Test
  void test1M_21_addAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.add("X"),
        "add after add");
  } // test1M_21

  @Test
  void test1M_22_nextAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.next(),
        "next after add");
  } // test1M_22

  @Test
  void test1M_23_prevAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.previous(),
        "prev after add");
  } // test1M_23

  @Test
  void test1M_23_hasNextAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.hasNext(),
        "hasNext after add");
  } // test1M_23

  @Test
  void test1M_24_hasPrevAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.hasPrevious(),
        "hasPrev after add");
  } // test1M_24

  @Test
  void test1M_24_nextIndexAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.nextIndex(),
        "nextIndex after add");
  } // test1M_24

  @Test
  void test1M_25_prevIndexAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.previousIndex(),
        "prevIndex after add");
  } // test1M_25

  @Test
  void test1M_25_setAfterAdd() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.add("X");
    assertThrows(ConcurrentModificationException.class,
        () -> lit2.set("X"),
        "prevIndex after add");
  } // test1M_25

  @Test
  void test1M_26_nextIndex() {
    ListIterator<String> lit = strings.listIterator();
    int pos = 0;
    while (lit.hasNext()) {
      assertEquals(pos++, lit.nextIndex(), "nextPosition: " + pos);
      lit.next();
    }
    assertEquals(pos, lit.nextIndex(), "nextPostion: " + pos);
  } // test1M_26

  @Test
  void test1M_27_prevIndex() {
    ListIterator<String> lit = strings.listIterator();
    int pos = -1;
    while (lit.hasNext()) {
      assertEquals(pos++, lit.previousIndex(), "previousPosition: " + pos);
      lit.next();
    }
    assertEquals(pos, lit.previousIndex(), "previousPostion: " + pos);
  } // test1M_27

  // +---------------+-----------------------------------------------
  // | E-level tests |
  // +---------------+

  @Test
  void test2E_01_nextAfterSet() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit1.next();
    lit1.set("X");
    assertEquals("X", lit2.next(), "next after set");
  } // test2E_01

  @Test
  void test2E_02_prevAfterSet() {
    ListIterator<String> lit1 = strings.listIterator();
    ListIterator<String> lit2 = strings.listIterator();
    lit2.next();
    lit1.next();
    lit1.set("X");
    assertEquals("X", lit2.previous(), "prev after set");
  } // test2E_02
} // class SimpleCDLLTests
