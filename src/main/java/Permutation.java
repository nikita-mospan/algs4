import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);

        RandomizedQueue<String> randomizedQueue = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            randomizedQueue.enqueue(item);
        }

        final Iterator<String> iterator = randomizedQueue.iterator();

        int i = 1;
        while (iterator.hasNext()) {
            if (i > k) {
                break;
            }
            System.out.println(iterator.next());
            i++;
        }
    }
}
