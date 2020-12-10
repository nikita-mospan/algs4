import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node<Item> first;    // beginning of deque
    private Node<Item> last;     // end of deque
    private int n;               // number of elements on deque

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
        private Node<Item> prev;
    }

    // construct an empty deque
    public Deque() {
        first = null;
        last = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    private void validateItemNotNull(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item must not be null when calling addFirst or addLast!");
        }
    }

    // add the item to the front
    public void addFirst(Item item) {
        validateItemNotNull(item);
        Node<Item> oldFirst = first;
        first = new Node<>();
        first.item = item;
        first.next = oldFirst;
        first.prev = null;
        if (isEmpty()) {
            last = first;
        } else {
            oldFirst.prev = first;
        }
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        validateItemNotNull(item);
        Node<Item> oldLast = last;
        last = new Node<>();
        last.item = item;
        last.next = null;
        last.prev = oldLast;
        if (isEmpty()) {
            first = last;
        } else {
            oldLast.next = last;
        }
        n++;
    }

    private void validateDequeNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque must be non empty when calling removeFirst or removeLast.");
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {
        validateDequeNotEmpty();
        Item item = first.item;
        first = first.next;
        n--;
        if (isEmpty()) {
            last = null;
        } else {
            first.prev = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        validateDequeNotEmpty();
        Item item = last.item;
        last = last.prev;
        n--;
        if (isEmpty()) {
            first = null;
        } else {
            last.next = null;
        }
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {

        private Node<Item> current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (current == null) {
                throw new NoSuchElementException("no more items left in iterator");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove method is not supported!");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> integerDeque = new Deque<>();
        integerDeque.addFirst(45);
        integerDeque.removeFirst();
        integerDeque.addLast(5);
        integerDeque.addLast(12);
        System.out.println(integerDeque.isEmpty());
        System.out.println(integerDeque.size());
        System.out.println(integerDeque.removeFirst());
        System.out.println(integerDeque.removeLast());
        System.out.println(integerDeque.size());
        for (Integer item : integerDeque) {
            System.out.println(item);
        }
    }

}
