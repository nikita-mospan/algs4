import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SAP {

    private final Digraph digraph;
    private final boolean[] markedV;
    private final boolean[] markedW;
    private final  int[] distToV;
    private final int[] distToW;
    private final Set<Integer> prevChangedIndexesMarkedV;
    private final Set<Integer> prevChangedIndexesMarkedW;
    private final Set<Integer> prevChangedIndexesDistToV;
    private final Set<Integer> prevChangedIndexesDistToW;


    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Argument G must not be null!");
        }
        digraph = new Digraph(G);

        markedV = new boolean[digraph.V()];
        markedW = new boolean[digraph.V()];
        distToV = new int[digraph.V()];
        distToW = new int[digraph.V()];


        prevChangedIndexesMarkedV = new HashSet<>();
        prevChangedIndexesMarkedW = new HashSet<>();
        prevChangedIndexesDistToV = new HashSet<>();
        prevChangedIndexesDistToW = new HashSet<>();
    }

    private static final class LengthAndAncestor {
        private final int length;
        private final int ancestor;

        public LengthAndAncestor(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
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

    private void initQueueForBfs(Queue<Integer> q, boolean[] marked, int[] distTo, Iterable<Integer> sources, boolean isV) {
        Set<Integer> prevChangedIndexesMarked;
        Set<Integer> prevChangedIndexesDistTo;
        if (isV) {
            prevChangedIndexesMarked = prevChangedIndexesMarkedV;
            prevChangedIndexesDistTo = prevChangedIndexesDistToV;
        } else {
            prevChangedIndexesMarked = prevChangedIndexesMarkedW;
            prevChangedIndexesDistTo = prevChangedIndexesDistToW;
        }
        for (Integer prevMarkedIndex : prevChangedIndexesMarked) {
            marked[prevMarkedIndex] = false;
        }
        for (Integer prevDistToIndex : prevChangedIndexesDistTo) {
            distTo[prevDistToIndex] = 0;
        }
        prevChangedIndexesMarked.clear();
        prevChangedIndexesDistTo.clear();
        for (int s : sources) {
            marked[s] = true;
            distTo[s] = 0;
            prevChangedIndexesMarked.add(s);
            prevChangedIndexesDistTo.add(s);
            q.enqueue(s);
        }
    }

    private void finishBfs(Queue<Integer> q) {
        while (!q.isEmpty()) {
            q.dequeue();
        }
    }

    private LengthAndAncestor doSingleBfsStep(Queue<Integer> q,
                                              boolean[] marked,
                                              int[] distTo,
                                              boolean[] otherMarked,
                                              int[] otherDistTo,
                                              int minLength,
                                              boolean firstAncestorFound,
                                              boolean doBfsStepFromV) {
        if (q.isEmpty()) {
            return null;
        }
        Set<Integer> prevChangedIndexesMarked;
        Set<Integer> prevChangedIndexesDistTo;
        if (doBfsStepFromV) {
            prevChangedIndexesMarked = prevChangedIndexesMarkedV;
            prevChangedIndexesDistTo = prevChangedIndexesDistToV;
        } else {
            prevChangedIndexesMarked = prevChangedIndexesMarkedW;
            prevChangedIndexesDistTo = prevChangedIndexesDistToW;
        }
        int v = q.dequeue();
        LengthAndAncestor minLengthAndAncestor = null;
        for (int adjV : digraph.adj(v)) {
            if (!marked[adjV]) {
                distTo[adjV] = distTo[v] + 1;
                marked[adjV] = true;
                prevChangedIndexesDistTo.add(adjV);
                prevChangedIndexesMarked.add(adjV);
                if (otherMarked[adjV]) {
                    if (distTo[adjV] > minLength && firstAncestorFound) {
                        finishBfs(q);
                        return null;
                    }
                    int curLength = distTo[adjV] + otherDistTo[adjV];
                    if (curLength < minLength || minLength == -1) {
                        minLength = curLength;
                        minLengthAndAncestor = new LengthAndAncestor(curLength, adjV);
                    }
                }
                q.enqueue(adjV);
            }
        }
        return minLengthAndAncestor;
    }

    private LengthAndAncestor computeLengthAndAncestor(Iterable<Integer> iterableV, Iterable<Integer> iterableW) {
        for (Integer vItem : iterableV) {
            for (Integer wItem : iterableW) {
                if (vItem.equals(wItem)) {
                    return new LengthAndAncestor(0, vItem);
                }
            }
        }

        Queue<Integer> qV = new Queue<>();
        initQueueForBfs(qV, markedV, distToV, iterableV, true);
        Queue<Integer> qW = new Queue<>();
        initQueueForBfs(qW, markedW, distToW, iterableW, false);

        boolean doBfsStepFromV = true;
        boolean firstAncestorFound = false;
        LengthAndAncestor minLengthAndAncestor = new LengthAndAncestor(-1, -1);
        while (!qV.isEmpty() || !qW.isEmpty()) {
            final int minLength = minLengthAndAncestor.length;
            final LengthAndAncestor lengthAndAncestor = doBfsStepFromV
                    ? doSingleBfsStep(qV, markedV, distToV, markedW, distToW, minLength, firstAncestorFound, true)
                    : doSingleBfsStep(qW, markedW, distToW, markedV, distToV, minLength, firstAncestorFound, false);
            if (firstAncestorFound && lengthAndAncestor != null && lengthAndAncestor.length < minLength) {
                minLengthAndAncestor = lengthAndAncestor;
            } else if (lengthAndAncestor != null) {
                firstAncestorFound = true;
                minLengthAndAncestor = lengthAndAncestor;
            }
            doBfsStepFromV = !doBfsStepFromV;
        }

        return minLengthAndAncestor;
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
        final LengthAndAncestor lengthAndAncestor = computeLengthAndAncestor(v, w);
        return lengthAndAncestor.length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateIterables(v, w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) {
            return -1;
        }
        final LengthAndAncestor lengthAndAncestor = computeLengthAndAncestor(v, w);
        return lengthAndAncestor.ancestor;
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
