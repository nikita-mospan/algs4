import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    // initial capacity of underlying resizing array
    private static final int INIT_CAPACITY = 8;

    private Item[] q;       // queue elements
    private int n;          // number of elements on queue
    private int first;      // index of first element of queue
    private int last;       // index of next available slot


    // construct an empty randomized queue
    public RandomizedQueue() {
        // noinspection unchecked
        q = (Item[]) new Object[INIT_CAPACITY];
        n = 0;
        first = 0;
        last = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // resize the underlying array
    private void resize(int capacity) {
        assert capacity >= n;
        // noinspection unchecked
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            copy[i] = q[(first + i) % q.length];
        }
        q = copy;
        first = 0;
        last  = n;
    }

    private void validateItemNotNull(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item must not be null when calling addFirst or addLast!");
        }
    }

    // add the item
    public void enqueue(Item item) {
        validateItemNotNull(item);
        // double size of array if necessary and recopy to front of array
        if (n == q.length) resize(2*q.length);   // double size of array if necessary
        q[last++] = item;                        // add item
        if (last == q.length) last = 0;          // wrap-around
        n++;
    }

    private void validateQueueNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue must be non empty when calling dequeue or sample.");
        }
    }

    private void swap(int i, int j) {
        Item temp = q[i];
        q[i] = q[j];
        q[j] = temp;
    }

    private int getRandomIdx() {
        return (first + StdRandom.uniform(n)) % q.length;
    }

    // remove and return a random item
    public Item dequeue() {
        validateQueueNotEmpty();
        swap(first, getRandomIdx());
        Item item = q[first];
        q[first] = null;                            // to avoid loitering
        n--;
        first++;
        if (first == q.length) first = 0;           // wrap-around
        // shrink size of array if necessary
        if (n > 0 && n == q.length/4) resize(q.length/2);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        validateQueueNotEmpty();
        return q[getRandomIdx()];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {


        private final Item[] randomArray;
        private int curIdx;

        public RandomizedQueueIterator() {
            // noinspection unchecked
            randomArray = (Item[]) new Object[n];
            for (int i = 0; i < n; i++) {
                randomArray[i] = q[(first + i) % q.length];
            }
            StdRandom.shuffle(randomArray);
            curIdx = 0;
        }

        @Override
        public boolean hasNext() {
            return curIdx < n;
        }

        @Override
        public Item next() {
            if (curIdx >= n) {
                throw new NoSuchElementException("no more items left in iterator");
            }
            Item item = randomArray[curIdx];
            curIdx++;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove method is not supported!");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> randomizedQueue = new RandomizedQueue<>();
        for (int i = 0; i < 10; i++) {
            randomizedQueue.enqueue(i);
        }
        System.out.println("isEmpty: " + randomizedQueue.isEmpty());
        System.out.println("size: " + randomizedQueue.size());
        System.out.println("sample: " + randomizedQueue.sample());
        System.out.println("deq: " + randomizedQueue.dequeue());
        for (Integer item : randomizedQueue) {
            System.out.println("item: " + item);
        }
    }

}
