import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static char[] getAlphabet() {
        // extended ASCII
        final int R = 256;
        final char[] alphabet = new char[R];
        for (char i = 0; i < R; i++) {
            alphabet[i] = i;
        }

        return alphabet;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        final char[] charToIdx = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            char curChar = BinaryStdIn.readChar();
            char curIdx = charToIdx[curChar];
            BinaryStdOut.write(curIdx);
            char numberOfShifts = 0;
            for (char i = 0; i < charToIdx.length; i++) {
                if (numberOfShifts == curIdx) {
                    break;
                }
                if (charToIdx[i] < curIdx) {
                    charToIdx[i]++;
                    numberOfShifts++;
                }
            }
            charToIdx[curChar] = 0;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        final char[] idxToChar = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            char curIdx = BinaryStdIn.readChar();
            char curChar = idxToChar[curIdx];
            BinaryStdOut.write(curChar);
            System.arraycopy(idxToChar, 0, idxToChar, 1, curIdx);
            idxToChar[0] = curChar;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        if (args[0].equals("+")) {
            decode();
        }
    }

}
