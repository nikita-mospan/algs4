import edu.princeton.cs.algs4.Bag;

import java.util.Arrays;

public class Board {

    private final int[][] tiles;
    private final int n;

    private static int getGoalValue(int n, int row, int col) {
        return row * n + col + 1;
    }

    private static void swap(int[][] a, int row1, int col1, int row2, int col2) {
        int temp = a[row1][col1];
        a[row1][col1] = a[row2][col2];
        a[row2][col2] = temp;
    }

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("tiles must not be null!");
        }
        this.n = tiles.length;
        this.tiles = new int[n][n];
        for (int row = 0; row < n; row++) {
            this.tiles[row] = tiles[row].clone();
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder result = new StringBuilder();
        int maxNumberOfDigits = (int) (Math.log10(n * n - 1) + 1);
        result.append(n).append("\n");
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                result.append(String.format("%" + (maxNumberOfDigits + 1) + "d", tiles[row][col]));
            }
            result.append("\n");
        }
        return result.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int result = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (tiles[row][col] != getGoalValue(n, row, col) && !(row == n - 1 && col == n - 1)) {
                    result++;
                }
            }
        }
        return result;
    }

    private int[] getRowCol(int value) {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (tiles[row][col] == value) {
                    return new int[]{row, col};
                }
            }
        }
        throw new RuntimeException(String.format("Value %d was not found in tiles array", value));
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int result = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (tiles[row][col] != getGoalValue(n, row, col) && !(row == n - 1 && col == n - 1)) {
                    int[] rowCol = getRowCol(getGoalValue(n, row, col));
                    result += Math.abs(row - rowCol[0]) + Math.abs(col - rowCol[1]);
                }
            }
        }
        return result;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                    if (tiles[row][col] != getGoalValue(n, row, col) && !(row == n-1 && col == n - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Board board = (Board) other;

        if (n != board.n) return false;
        return Arrays.deepEquals(tiles, board.tiles);
    }

    private void addToNeighborsBag(int row1, int col1, int row2, int col2, Bag<Board> neighborsBag) {
        int[][] newTiles = new int[n][n];
        for (int row = 0; row < n; row++) {
            newTiles[row] = tiles[row].clone();
        }
        if (row1 >= 0 && row1 < n && col1 >= 0 && col1 < n && row2 >= 0 && row2 < n && col2 >= 0 && col2 < n) {
            swap(newTiles, row1, col1, row2, col2);
            neighborsBag.add(new Board(newTiles));
        }
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Bag<Board> neighborsBag = new Bag<>();
        final int[] rowCol = getRowCol(0);
        int row = rowCol[0];
        int col = rowCol[1];
        addToNeighborsBag(row, col, row, col - 1, neighborsBag);
        addToNeighborsBag(row, col, row - 1, col, neighborsBag);
        addToNeighborsBag(row, col, row, col + 1, neighborsBag);
        addToNeighborsBag(row, col, row + 1, col, neighborsBag);
        return neighborsBag;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] newTiles = new int[n][n];
        for (int row = 0; row < n; row++) {
            newTiles[row] = tiles[row].clone();
        }
        int row1 = -1;
        int col1 = -1;
        int row2 = -1;
        int col2 = -1;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (row1 >= 0 && col1 >= 0 && row2 >= 0 && col2 >= 0) {
                    break;
                }
                if (newTiles[row][col] != 0) {
                    if (row1 < 0 && col1 < 0) {
                        row1 = row;
                        col1 = col;
                    } else {
                        row2 = row;
                        col2 = col;
                    }
                }
            }
        }
        swap(newTiles, row1, col1, row2, col2);
        return new Board(newTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] tiles = {{8, 1, 3}, {4, 0, 2}, {7, 6, 5}};
        Board board = new Board(tiles);
        System.out.println(board);
        System.out.println(board.dimension());
        System.out.println("isGoal: " + board.isGoal());
        System.out.println("haming: " + board.hamming());
        System.out.println("manhattan: " + board.manhattan());
        System.out.println("Neighbors:------");
        for (Board neighbor : board.neighbors()) {
            System.out.println(neighbor);
        }
        System.out.println("-------------------");
        System.out.println("twin: " + board.twin());
    }

}
