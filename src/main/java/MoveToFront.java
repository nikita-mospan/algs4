import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // extended ASCII
        final int R = 256;
        final char[] alphabet = new char[R];
        for (char i = 0; i < R; i++) {
            alphabet[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            char idxOfC = alphabet[c];
            BinaryStdOut.write(idxOfC);
            for (char i = 0; i < R; i++) {
                if (alphabet[i] < idxOfC) {
                    alphabet[i]++;
                }
            }
            alphabet[c] = 0;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
    }

}
