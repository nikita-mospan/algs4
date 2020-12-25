import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SAP {

    private final Digraph digraph;
    private final Map<LengthAndAncestorSingle, LengthAndAncestorSingle> lengthAndAncestorSingleMap;
    private final Map<LengthAndAncestorIterable, LengthAndAncestorIterable> lengthAndAncestorIterableMap;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Argument G must not be null!");
        }
        digraph = G;
        lengthAndAncestorSingleMap = new HashMap<>();
        lengthAndAncestorIterableMap = new HashMap<>();
    }

    private static final class LengthAndAncestor {
        private final int length;
        private final int ancestor;

        public LengthAndAncestor(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }

        public int getLength() {
            return length;
        }

        public int getAncestor() {
            return ancestor;
        }
    }

    private static class LengthAndAncestorSingle {
        private final int v;
        private final int w;
        private final LengthAndAncestor lengthAndAncestor;

        public LengthAndAncestorSingle(int v, int w) {
            this.v = v;
            this.w = w;
            lengthAndAncestor = new LengthAndAncestor(-1, -1);
        }

        public LengthAndAncestorSingle(int v, int w, LengthAndAncestor lengthAndAncestor) {
            this.v = v;
            this.w = w;
            this.lengthAndAncestor = lengthAndAncestor;
        }

        public int getLength() {
            return lengthAndAncestor.getLength();
        }

        public int getAncestor() {
            return lengthAndAncestor.getAncestor();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LengthAndAncestorSingle that = (LengthAndAncestorSingle) o;
            return v == that.v && w == that.w;
        }

        @Override
        public int hashCode() {
            return Objects.hash(v, w);
        }
    }

    private static class LengthAndAncestorIterable {
        private final Iterable<Integer> v;
        private final Iterable<Integer> w;
        private final LengthAndAncestor lengthAndAncestor;

        public LengthAndAncestorIterable(Iterable<Integer> v, Iterable<Integer> w) {
            this.v = v;
            this.w = w;
            lengthAndAncestor = new LengthAndAncestor(-1, -1);
        }

        public LengthAndAncestorIterable(Iterable<Integer> v, Iterable<Integer> w, LengthAndAncestor lengthAndAncestor) {
            this.v = v;
            this.w = w;
            this.lengthAndAncestor = lengthAndAncestor;
        }

        public int getLength() {
            return lengthAndAncestor.getLength();
        }

        public int getAncestor() {
            return lengthAndAncestor.getAncestor();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LengthAndAncestorIterable that = (LengthAndAncestorIterable) o;
            return v.equals(that.v) && w.equals(that.w);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v, w);
        }
    }

    private LengthAndAncestor computeLengthAndAncestor(BreadthFirstDirectedPaths breadthFirstDirectedPathsV,
                                                       BreadthFirstDirectedPaths breadthFirstDirectedPathsW) {
        int minDistance = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int vertex = 0; vertex < digraph.V(); vertex++) {
            if (breadthFirstDirectedPathsV.pathTo(vertex) != null && breadthFirstDirectedPathsW.pathTo(vertex) != null) {
                int curDistance = breadthFirstDirectedPathsV.distTo(vertex) + breadthFirstDirectedPathsW.distTo(vertex);
                if (curDistance < minDistance) {
                    ancestor = vertex;
                    minDistance = curDistance;
                }
            }
        }

        if (minDistance == Integer.MAX_VALUE) {
            minDistance = -1;
        }
        return new LengthAndAncestor(minDistance, ancestor);
    }

    private LengthAndAncestorSingle computeLengthAndAncestorSingle (int v, int w) {
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsV = new BreadthFirstDirectedPaths(digraph, v);
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsW = new BreadthFirstDirectedPaths(digraph, w);
        LengthAndAncestor lengthAndAncestor = computeLengthAndAncestor(breadthFirstDirectedPathsV, breadthFirstDirectedPathsW);

        return new LengthAndAncestorSingle(v, w, lengthAndAncestor);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        final LengthAndAncestorSingle lengthAndAncestorSingle =
                lengthAndAncestorSingleMap.computeIfAbsent(new LengthAndAncestorSingle(v, w), key -> computeLengthAndAncestorSingle(v, w));
        lengthAndAncestorSingleMap.put(lengthAndAncestorSingle, lengthAndAncestorSingle);
        return lengthAndAncestorSingle.getLength();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        final LengthAndAncestorSingle lengthAndAncestorSingle =
                lengthAndAncestorSingleMap.computeIfAbsent(new LengthAndAncestorSingle(v, w), key -> computeLengthAndAncestorSingle(v, w));
        lengthAndAncestorSingleMap.put(lengthAndAncestorSingle, lengthAndAncestorSingle);
        return lengthAndAncestorSingle.getAncestor();
    }

    private static void checkNounsNotNull(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Arguments tp distance must not be null!");
        }
    }

    private LengthAndAncestorIterable computeLengthAndAncestorIterable (Iterable<Integer> v, Iterable<Integer> w) {
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsV = new BreadthFirstDirectedPaths(digraph, v);
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsW = new BreadthFirstDirectedPaths(digraph, w);

        LengthAndAncestor lengthAndAncestor = computeLengthAndAncestor(breadthFirstDirectedPathsV, breadthFirstDirectedPathsW);
        return new LengthAndAncestorIterable(v, w, lengthAndAncestor);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkNounsNotNull(v, w);
        final LengthAndAncestorIterable lengthAndAncestorIterable =
                lengthAndAncestorIterableMap.computeIfAbsent(new LengthAndAncestorIterable(v, w), key -> computeLengthAndAncestorIterable(v, w));
        lengthAndAncestorIterableMap.put(lengthAndAncestorIterable, lengthAndAncestorIterable);
        return lengthAndAncestorIterable.getLength();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkNounsNotNull(v, w);
        final LengthAndAncestorIterable lengthAndAncestorIterable =
                lengthAndAncestorIterableMap.computeIfAbsent(new LengthAndAncestorIterable(v, w), key -> computeLengthAndAncestorIterable(v, w));
        lengthAndAncestorIterableMap.put(lengthAndAncestorIterable, lengthAndAncestorIterable);
        return lengthAndAncestorIterable.getAncestor();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}