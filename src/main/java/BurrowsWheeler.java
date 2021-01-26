import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {
            final String readString = BinaryStdIn.readString();
            final int length = readString.length();
            final CircularSuffixArray circularSuffixArray = new CircularSuffixArray(readString);
            final char[] transformCharArray = new char[length];
            int first = -1;
            for (int i = 0; i < length; i++) {
                final int circularSuffixArrayIndex = circularSuffixArray.index(i);
                if (circularSuffixArrayIndex == 0) {
                    first = i;
                    transformCharArray[i] = readString.charAt(length - 1);
                } else {
                    transformCharArray[i] = readString.charAt((circularSuffixArrayIndex % length) - 1);
                }
            }
            if (first == -1) {
                throw new IllegalStateException("first variable must be positive");
            }
            BinaryStdOut.write(first);
            for (char c : transformCharArray) {
                BinaryStdOut.write(c);
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        while (!BinaryStdIn.isEmpty()) {
            final int first = BinaryStdIn.readInt();
            final String encodedString = BinaryStdIn.readString();
            final int length = encodedString.length();
            int R = 256;   // extend ASCII alphabet size
            char[] aux = new char[length];
            int[] count = new int[R+1];
            int[] next = new int[length];

            for (int i = 0; i < length; i++)
                count[encodedString.charAt(i) + 1]++;

            // compute cumulates
            for (int r = 0; r < R; r++)
                count[r+1] += count[r];

            // compute next
            for (int i = 0; i < length; i++) {
                aux[count[encodedString.charAt(i)]] = encodedString.charAt(i);
                next[count[encodedString.charAt(i)]] = i;
                count[encodedString.charAt(i)] = count[encodedString.charAt(i)] + 1;
            }

            BinaryStdOut.write(aux[first]);
            int nextPos = next[first];
            for (int i = 1; i < length - 1; i++) {
                BinaryStdOut.write(aux[nextPos]);
                nextPos = next[nextPos];
            }
            BinaryStdOut.write(encodedString.charAt(first));
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        if (args[0].equals("+")) {
            inverseTransform();
        }
    }

}
