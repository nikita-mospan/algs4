import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private static class RowColIds {
        private final int rowId;
        private final int colId;

        public RowColIds(int rowId, int colId) {
            this.rowId = rowId;
            this.colId = colId;
        }
    }

    private final TrieSetAZ trieSetAZ;

    public BoggleSolver(String[] dictionary) {
        trieSetAZ = new TrieSetAZ();
        for (String word : dictionary) {
            trieSetAZ.add(word);
        }
    }

    private Iterable<Integer> getAdjacentDies(BoggleBoard board, int rowId, int colId) {
        Bag<Integer> adjacentLetters = new Bag<>();
        if (rowId > 0) {
            adjacentLetters.add(getDieId(board.rows(), rowId - 1, colId));
        }
        if (colId > 0) {
            adjacentLetters.add(getDieId(board.rows(), rowId, colId - 1));
        }
        if (rowId > 0 && colId > 0) {
            adjacentLetters.add(getDieId(board.rows(), rowId - 1, colId - 1));
        }
        if (rowId < board.rows() - 1) {
            adjacentLetters.add(getDieId(board.rows(), rowId + 1, colId));
        }
        if (colId < board.cols() - 1) {
            adjacentLetters.add(getDieId(board.rows(), rowId, colId + 1));
        }
        if (rowId < board.rows() - 1 && colId < board.cols() - 1) {
            adjacentLetters.add(getDieId(board.rows(), rowId + 1, colId + 1));
        }
        if (rowId > 0 && colId < board.cols() - 1) {
            adjacentLetters.add(getDieId(board.rows(), rowId - 1, colId + 1));
        }
        if (rowId < board.rows() - 1 && colId > 0) {
            adjacentLetters.add(getDieId(board.rows(), rowId + 1, colId - 1));
        }
        return adjacentLetters;
    }

    private int getDieId(int rows, int rowId, int colId) {
        return rows * rowId + colId;
    }

    private RowColIds getRowColFromDieId(int cols, int dieId) {
        return new RowColIds(dieId / cols, dieId % cols);
    }

    private String charStackToString(Stack<Character> charStack) {
        char[] charArray = new char[charStack.size()];
        int i = charStack.size() - 1;
        for (Character character : charStack) {
            charArray[i] = character;
            i--;
        }
        return new String(charArray);
    }

    private void populateValidWordsFromSingleDie(BoggleBoard board, int rowId, int colId, Stack<Character> charStack,
                                                 Set<String> validWords, boolean[] marked) {

        marked[getDieId(board.rows(), rowId, colId)] = true;
        charStack.push(board.getLetter(rowId, colId));
        final String curPrefix = charStackToString(charStack);
        if (curPrefix.length() >= 3 && trieSetAZ.contains(curPrefix)) {
            validWords.add(curPrefix);
        }
        if (trieSetAZ.keysWithPrefix(curPrefix).iterator().hasNext()) {
            for (int dieId : getAdjacentDies(board, rowId, colId)) {
                RowColIds rowColIds = getRowColFromDieId(board.cols(), dieId);
                int adjRowid = rowColIds.rowId;
                int adjColId = rowColIds.colId;
                if (!marked[dieId]) {
                    populateValidWordsFromSingleDie(board, adjRowid, adjColId, charStack, validWords, marked);
                }
            }
        }

        charStack.pop();
        marked[getDieId(board.rows(), rowId, colId)] = false;

    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        final Set<String> allValidWords = new HashSet<>();
        final boolean[] marked = new boolean[board.rows() * board.cols()];

        for (int rowId = 0; rowId < board.rows(); rowId++) {
            for (int colId = 0; colId < board.cols(); colId++) {
                Arrays.fill(marked, false);
                Stack<Character> charStack = new Stack<>();
                populateValidWordsFromSingleDie(board, rowId, colId, charStack, allValidWords, marked);
            }
        }

        return allValidWords;
    }

    public int scoreOf(String word) {
        switch (word.length()) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}
