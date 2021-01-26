import java.util.Iterator;
import java.util.TreeSet;

public class CircularSuffixArray {

    private final int n;
    private final CircularSuffix[] arrayOfSortedCircularSuffixes;

    private static class CircularSuffix implements Comparable<CircularSuffix>, Iterable<Character> {
        private final String inputString;
        private final int startPositionInSuffix;

        private class CircularSuffixIterator implements Iterator<Character> {

            private final int length = inputString.length();
            private int numberOfCharsLeft = length;
            private int curPosition = startPositionInSuffix;

            @Override
            public boolean hasNext() {
                return numberOfCharsLeft > 0;
            }

            @Override
            public Character next() {
                char curChar = inputString.charAt(curPosition);
                curPosition = (curPosition + 1) % length;
                numberOfCharsLeft--;
                return curChar;
            }
        }

        public CircularSuffix(String s, int startPositionInSuffix) {
            inputString = s;
            this.startPositionInSuffix = startPositionInSuffix;
        }

        @Override
        public int compareTo(CircularSuffix other) {
            Iterator<Character> thisIterator = iterator();
            Iterator<Character> otherIterator = other.iterator();
            while (thisIterator.hasNext()) {
                Character thisChar = thisIterator.next();
                Character otherChar = otherIterator.next();
                final int compareResult = thisChar.compareTo(otherChar);
                if (compareResult != 0) {
                    return compareResult;
                }
            }
            return 0;
        }

        @Override
        public Iterator<Character> iterator() {
            return new CircularSuffixIterator();
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s argument must not be null");
        }
        n = s.length();
        arrayOfSortedCircularSuffixes = new CircularSuffix[n];
        final TreeSet<CircularSuffix> circularSuffixTreeSet = new TreeSet<>(CircularSuffix::compareTo);

        for (int idx = 0; idx < n; idx++) {
            circularSuffixTreeSet.add(new CircularSuffix(s, idx));
        }

        int idx = 0;
        for (CircularSuffix circularSuffix : circularSuffixTreeSet) {
            arrayOfSortedCircularSuffixes[idx] = circularSuffix;
            idx++;
        }
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int  index(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException("i argument must be between 0 and " + (n - 1));
        }
        return arrayOfSortedCircularSuffixes[i].startPositionInSuffix;

    }

    // unit testing (required)
    public static void main(String[] args) {

        String s = "ABRACADABRA!";
        final CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        System.out.println(circularSuffixArray.length());
        System.out.println(circularSuffixArray.index(6));

    }

}
