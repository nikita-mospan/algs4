import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class SAP {

    private final Digraph digraph;
    private final List<LengthAndAncestorOfIterables> lengthAndAncestorOfIterablesList;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Argument G must not be null!");
        }
        digraph = G;
        lengthAndAncestorOfIterablesList = new ArrayList<>();
    }

    private static class LengthAndAncestorOfIterables {
        private final Iterable<Integer> v;
        private final Iterable<Integer> w;
        private final int length;
        private final int ancestor;

        public LengthAndAncestorOfIterables(Iterable<Integer> v, Iterable<Integer> w) {
            this.v = v;
            this.w = w;
            length = -1;
            ancestor = -1;
        }

        public LengthAndAncestorOfIterables(Iterable<Integer> v, Iterable<Integer> w, int length, int ancestor) {
            this.v = v;
            this.w = w;
            this.length = length;
            this.ancestor = ancestor;
        }

        public int getLength() {
            return length;
        }

        public int getAncestor() {
            return ancestor;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            LengthAndAncestorOfIterables that = (LengthAndAncestorOfIterables) other;
            return v.equals(that.v) && w.equals(that.w);
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "LengthAndAncestorOfIterables{" +
                    "v=" + v +
                    ", w=" + w +
                    ", length=" + length +
                    ", ancestor=" + ancestor +
                    '}';
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        List<Integer> iterableV = new ArrayList<>();
        iterableV.add(v);
        List<Integer> iterableW = new ArrayList<>();
        iterableW.add(w);
        return length(iterableV, iterableW);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        List<Integer> iterableV = new ArrayList<>();
        iterableV.add(v);
        List<Integer> iterableW = new ArrayList<>();
        iterableW.add(w);
        return ancestor(iterableV, iterableW);
    }

    private static void checkNounsNotNull(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Arguments tp distance must not be null!");
        }
    }

    private void initQueueForBfs(Queue<Integer> q, boolean[] marked, int[] distTo, Iterable<Integer> sources) {
        for (int s : sources) {
            marked[s] = true;
            distTo[s] = 0;
            q.enqueue(s);
        }
    }

    private void finishBfs(Queue<Integer> q) {
        while (!q.isEmpty()) {
            q.dequeue();
        }
    }

    private LengthAndAncestorOfIterables doSingleBfsStep(Iterable<Integer> iterableV,
                                                         Iterable<Integer> iterableW,
                                                         Queue<Integer> q,
                                                         boolean[] marked,
                                                         int[] distTo,
                                                         boolean[] otherMarked,
                                                         int[] otherDistTo,
                                                         int minLength,
                                                         boolean firstAncestorFound) {
        if (q.isEmpty()) {
            return null;
        }
        int v = q.dequeue();
        LengthAndAncestorOfIterables minLengthAndAncestorOfIterables = null;
        for (int adjV : digraph.adj(v)) {
            if (!marked[adjV]) {
                distTo[adjV] = distTo[v] + 1;
                marked[adjV] = true;
                if (otherMarked[adjV]) {
                    if (distTo[adjV] > minLength && firstAncestorFound) {
                        finishBfs(q);
                        return null;
                    }
                    int curLength = distTo[adjV] + otherDistTo[adjV];
                    if (curLength < minLength || minLength == -1) {
                        minLength = curLength;
                        minLengthAndAncestorOfIterables = new LengthAndAncestorOfIterables(iterableV, iterableW, minLength, adjV);
                    }
                }
                q.enqueue(adjV);
            }
        }
        return minLengthAndAncestorOfIterables;
    }

    private LengthAndAncestorOfIterables computeLengthAndAncestorIterable(Iterable<Integer> iterableV, Iterable<Integer> iterableW) {
        for (Integer vItem : iterableV) {
            for (Integer wItem : iterableW) {
                if (vItem.equals(wItem)) {
                    return new LengthAndAncestorOfIterables(iterableV, iterableW, 0, vItem);
                }
            }
        }

        boolean[] markedV = new boolean[digraph.V()];
        boolean[] markedW = new boolean[digraph.V()];
        int[] distToV = new int[digraph.V()];
        int[] distToW = new int[digraph.V()];

        Queue<Integer> qV = new Queue<>();
        initQueueForBfs(qV, markedV, distToV, iterableV);
        Queue<Integer> qW = new Queue<>();
        initQueueForBfs(qW, markedW, distToW, iterableW);

        boolean doBfsStepFromV = true;
        boolean firstAncestorFound = false;
        LengthAndAncestorOfIterables minLengthAndAncestorOfIterables = new LengthAndAncestorOfIterables(iterableV, iterableW, -1, -1);
        while (!qV.isEmpty() || !qW.isEmpty()) {
            final int minLength = minLengthAndAncestorOfIterables.getLength();
            final LengthAndAncestorOfIterables lengthAndAncestorOfIterables = doBfsStepFromV
                    ? doSingleBfsStep(iterableV, iterableW, qV, markedV, distToV, markedW, distToW, minLength, firstAncestorFound)
                    : doSingleBfsStep(iterableV, iterableW, qW, markedW, distToW, markedV, distToV, minLength, firstAncestorFound);
            if (firstAncestorFound && lengthAndAncestorOfIterables != null && lengthAndAncestorOfIterables.getLength() < minLength) {
                minLengthAndAncestorOfIterables = lengthAndAncestorOfIterables;
            } else if (lengthAndAncestorOfIterables != null) {
                firstAncestorFound = true;
                minLengthAndAncestorOfIterables = lengthAndAncestorOfIterables;
            }
            doBfsStepFromV = !doBfsStepFromV;
        }

        return minLengthAndAncestorOfIterables;
    }

    private void validateVertexes(Iterable<Integer> v) {
        for (Integer vItem : v) {
            if (vItem == null || vItem < 0 || vItem >= digraph.V()) {
                throw new IllegalArgumentException("Wrong vertex: " + vItem);
            }
        }
    }

    private void validateIterables(Iterable<Integer> v, Iterable<Integer> w) {
        checkNounsNotNull(v, w);
        validateVertexes(v);
        validateVertexes(w);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateIterables(v, w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) {
            return -1;
        }
        int index = lengthAndAncestorOfIterablesList.indexOf(new LengthAndAncestorOfIterables(v, w));
        if (index >= 0) {
            return lengthAndAncestorOfIterablesList.get(index).getLength();
        } else {
            final LengthAndAncestorOfIterables lengthAndAncestorOfIterables = computeLengthAndAncestorIterable(v, w);
            lengthAndAncestorOfIterablesList.add(lengthAndAncestorOfIterables);
            lengthAndAncestorOfIterablesList.add(new LengthAndAncestorOfIterables(w, v,
                    lengthAndAncestorOfIterables.getLength(), lengthAndAncestorOfIterables.getAncestor()));
            return lengthAndAncestorOfIterables.getLength();
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateIterables(v, w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) {
            return -1;
        }
        validateVertexes(v);
        validateVertexes(w);
        int index = lengthAndAncestorOfIterablesList.indexOf(new LengthAndAncestorOfIterables(v, w));
        if (index >= 0) {
            return lengthAndAncestorOfIterablesList.get(index).getAncestor();
        } else {
            final LengthAndAncestorOfIterables lengthAndAncestorOfIterables = computeLengthAndAncestorIterable(v, w);
            lengthAndAncestorOfIterablesList.add(lengthAndAncestorOfIterables);
            lengthAndAncestorOfIterablesList.add(new LengthAndAncestorOfIterables(w, v,
                    lengthAndAncestorOfIterables.getLength(), lengthAndAncestorOfIterables.getAncestor()));
            return lengthAndAncestorOfIterables.getAncestor();
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
