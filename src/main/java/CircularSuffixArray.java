import java.util.Arrays;
import java.util.NoSuchElementException;

public class CircularSuffixArray {

    private final int n;
    private final CircularSuffix[] arrayOfSortedCircularSuffixes;

    private static class CircularSuffix implements Comparable<CircularSuffix> {
        private final String inputString;
        private final int startPositionInSuffix;

        private class CircularSuffixIterator {

            private final int length = inputString.length();
            private int numberOfCharsLeft = length;
            private int curPosition = startPositionInSuffix;

            public boolean hasNext() {
                return numberOfCharsLeft > 0;
            }

            public char next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
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
            CircularSuffixIterator thisIterator = iterator();
            CircularSuffixIterator otherIterator = other.iterator();
            while (thisIterator.hasNext()) {
                char thisChar = thisIterator.next();
                char otherChar = otherIterator.next();
                final int compareResult = thisChar - otherChar;
                if (compareResult != 0) {
                    return compareResult;
                }
            }
            return 0;
        }

        private CircularSuffixIterator iterator() {
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

        for (int idx = 0; idx < n; idx++) {
            arrayOfSortedCircularSuffixes[idx] = new CircularSuffix(s, idx);
        }

        Arrays.sort(arrayOfSortedCircularSuffixes, CircularSuffix::compareTo);
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException("i argument must be between 0 and " + (n - 1));
        }
        return arrayOfSortedCircularSuffixes[i].startPositionInSuffix;

    }

    // unit testing (required)
    public static void main(String[] args) {

        String s = "**";
        final CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        System.out.println(circularSuffixArray.length());
        System.out.println(circularSuffixArray.index(1));

    }

}
