import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private static class SearchNode implements Comparable<SearchNode> {
        final Board board;
        final int numberOfMoves;
        final SearchNode prevNode;
        final int manhattan;
        final int priority;

        private SearchNode(Board board, int numberOfMoves, SearchNode prevNode, int manhattan) {
            this.board = board;
            this.numberOfMoves = numberOfMoves;
            this.prevNode = prevNode;
            this.manhattan = manhattan;
            this.priority = numberOfMoves + manhattan;
        }

        public Board getBoard() {
            return board;
        }

        public SearchNode getPrevNode() {
            return prevNode;
        }

        public int getNumberOfMoves() {
            return numberOfMoves;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public int compareTo(SearchNode other) {
            return Integer.compare(getPriority(), other.getPriority());
        }
    }

    private final SearchNode solutionSearchNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("initial must be noy null");
        }
        SearchNode searchNodeInitial = new SearchNode(initial, 0, null, initial.manhattan());
        MinPQ<SearchNode> searchNodeMinPQInitial = new MinPQ<>();
        searchNodeMinPQInitial.insert(searchNodeInitial);

        Board twinBoard = initial.twin();
        SearchNode searchNodeTwin = new SearchNode(twinBoard, 0, null, twinBoard.manhattan());
        MinPQ<SearchNode> searchNodeMinPQTwin = new MinPQ<>();
        searchNodeMinPQTwin.insert(searchNodeTwin);

        solutionSearchNode = solve(searchNodeMinPQInitial, searchNodeMinPQTwin);
    }

    private SearchNode solve(MinPQ<SearchNode> searchNodeMinPQInitial, MinPQ<SearchNode> searchNodeMinPQTwin) {
        boolean takeFromTwinPQ = false;
        while (true) {
            if (takeFromTwinPQ) {
                final SearchNode twinSearchNode = searchNodeMinPQTwin.delMin();
                if (twinSearchNode.getBoard().isGoal()) {
                    return null;
                }

                for (Board neighbor : twinSearchNode.getBoard().neighbors()) {
                    SearchNode prevNode = twinSearchNode.getPrevNode();
                    if (prevNode == null || !neighbor.equals(prevNode.getBoard())) {
                        searchNodeMinPQTwin.insert(new SearchNode(neighbor,
                                twinSearchNode.getNumberOfMoves() + 1,
                                twinSearchNode,
                                neighbor.manhattan()));
                    }
                }
                takeFromTwinPQ = false;
            } else {
                final SearchNode searchNode = searchNodeMinPQInitial.delMin();
                if (searchNode.getBoard().isGoal()) {
                    return searchNode;
                }
                for (Board neighbor : searchNode.getBoard().neighbors()) {
                    SearchNode prevNode = searchNode.getPrevNode();
                    if (prevNode == null || !neighbor.equals(prevNode.getBoard())) {
                        searchNodeMinPQInitial.insert(new SearchNode(neighbor,
                                searchNode.getNumberOfMoves() + 1,
                                searchNode,
                                neighbor.manhattan()));
                    }
                }
                takeFromTwinPQ = true;
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solutionSearchNode != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (isSolvable()) {
            return solutionSearchNode.getNumberOfMoves();
        } else {
            return -1;
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (isSolvable()) {
            Stack<Board> solution = new Stack<>();
            SearchNode curSearchNode = solutionSearchNode;
            solution.push(curSearchNode.getBoard());
            while (curSearchNode.getPrevNode() != null) {
                solution.push(curSearchNode.getPrevNode().getBoard());
                curSearchNode = curSearchNode.getPrevNode();
            }
            return solution;
        } else {
            return null;
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
