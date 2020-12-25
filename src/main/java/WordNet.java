import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class WordNet {

    private final Digraph digraph;
    private final Map<Integer, Set<String>> synsetIdToNouns;
    private final Set<String> nouns;
    private final Map<DistanceAndSap, DistanceAndSap> distanceAndSapMap;

    private static class DistanceAndSap {
        private final String nounA;
        private final String nounB;
        private final int distance;
        private final String sap;

        public DistanceAndSap(String nounA, String nounB) {
            this.nounA = nounA;
            this.nounB = nounB;
            this.distance = Integer.MAX_VALUE;
            this.sap = null;
        }

        public DistanceAndSap(String nounA, String nounB, int distance, String sap) {
            this.nounA = nounA;
            this.nounB = nounB;
            this.distance = distance;
            this.sap = sap;
        }

        public int getDistance() {
            return distance;
        }

        public String getSap() {
            return sap;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DistanceAndSap that = (DistanceAndSap) o;
            return nounA.equals(that.nounA) && nounB.equals(that.nounB);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nounA, nounB);
        }
    }

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Constructor arguments must not be null!");
        }

        synsetIdToNouns = new HashMap<>();
        nouns = new HashSet<>();
        distanceAndSapMap = new HashMap<>();

        final In synsetsIn = new In(synsets);
        while (synsetsIn.hasNextLine()) {
            String line = synsetsIn.readLine();
            String[] tokens = line.split(",");
            int synsetId = Integer.parseInt(tokens[0]);
            final Set<String> curSynsetNouns = Arrays.stream(tokens[1].split("\\s+")).collect(Collectors.toSet());
            nouns.addAll(curSynsetNouns);
            synsetIdToNouns.put(synsetId, curSynsetNouns);
        }

        digraph = new Digraph(synsetIdToNouns.size());

        final Set<Integer> nonRootSynsets = new HashSet<>();
        final In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String line = hypernymsIn.readLine();
            String[] tokens = line.split(",", 2);
            int synsetId = Integer.parseInt(tokens[0]);
            if (tokens.length == 2) {
                nonRootSynsets.add(synsetId);
                Arrays.stream(tokens[1].split(",")).map(Integer::parseInt).collect(Collectors.toList())
                        .forEach(hypernymId -> digraph.addEdge(synsetId, hypernymId));
            }
        }

        DirectedCycle directedCycle = new DirectedCycle(digraph);
        if ((synsetIdToNouns.size() - nonRootSynsets.size() > 1) || directedCycle.hasCycle()) {
            throw new IllegalArgumentException("digraph does not have single root");
        } else {
            System.out.println("Digraph is acyclic and has single root.");
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument to isNoun must not be null!");
        }
        return nouns.contains(word);
    }

    private static void checkNounsNotNull(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Arguments tp distance must not be null!");
        }
    }

    private Set<Integer> getSynsetsWithNoun(String noun) {
        return synsetIdToNouns.entrySet().stream().filter(entry -> entry.getValue().contains(noun))
                .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    private DistanceAndSap computeDistanceAndSap(String nounA, String nounB) {
        final Set<Integer> subsetA = getSynsetsWithNoun(nounA);
        final Set<Integer> subsetB = getSynsetsWithNoun(nounB);
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsA = new BreadthFirstDirectedPaths(digraph, subsetA);
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsB = new BreadthFirstDirectedPaths(digraph, subsetB);

        int minDistance = Integer.MAX_VALUE;
        String sap = null;
        for (int v = 0; v < digraph.V(); v++) {
            if (breadthFirstDirectedPathsA.pathTo(v) != null && breadthFirstDirectedPathsB.pathTo(v) != null) {
                int curDistance = breadthFirstDirectedPathsA.distTo(v) + breadthFirstDirectedPathsB.distTo(v);
                if (curDistance < minDistance) {
                    sap = String.join(" ", synsetIdToNouns.get(v));
                    minDistance = curDistance;
                }
            }
        }

        if (minDistance == Integer.MAX_VALUE) {
            minDistance = -1;
        }
        return new DistanceAndSap(nounA, nounB, minDistance, sap);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        final DistanceAndSap distanceAndSap = distanceAndSapMap.computeIfAbsent(new DistanceAndSap(nounA, nounB)
                , key -> computeDistanceAndSap(nounA, nounB));
        distanceAndSapMap.put(distanceAndSap, distanceAndSap);
        return distanceAndSap.getDistance();
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        final DistanceAndSap distanceAndSap = distanceAndSapMap.computeIfAbsent(new DistanceAndSap(nounA, nounB)
                , key -> computeDistanceAndSap(nounA, nounB));
        distanceAndSapMap.put(distanceAndSap, distanceAndSap);
        return distanceAndSap.getSap();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        final WordNet wordNet = new WordNet("synsets8.txt", "hypernyms8ManyAncestors.txt");
        System.out.printf("nouns: %s%n", String.join(", ", wordNet.nouns()));
        String someNoun = "banana";
        System.out.printf("%s is noun: %b%n", someNoun, wordNet.isNoun(someNoun));
        String nounA = "f";
        String nounB = "g";
        System.out.println(wordNet.sap(nounA, nounB));
        System.out.println(wordNet.distance(nounA, nounB));
    }
}