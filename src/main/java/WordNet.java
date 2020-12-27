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
    private final SAP sap;
    private final Map<DistanceForNouns, DistanceForNouns> distanceForNounsMap;
    private final Map<SapVertexForNouns, SapVertexForNouns> sapVertexForNounsMap;

    private static final class DistanceForNouns {
        private final String nounA;
        private final String nounB;
        private final int distance;

        public DistanceForNouns(String nounA, String nounB) {
            this.nounA = nounA;
            this.nounB = nounB;
            this.distance = -1;
        }

        public DistanceForNouns(String nounA, String nounB, int distance) {
            this.nounA = nounA;
            this.nounB = nounB;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DistanceForNouns that = (DistanceForNouns) o;
            return nounA.equals(that.nounA) && nounB.equals(that.nounB);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nounA, nounB);
        }
    }

    private static final class SapVertexForNouns {
        private final String nounA;
        private final String nounB;
        private final int sapVertex;

        public SapVertexForNouns(String nounA, String nounB) {
            this.nounA = nounA;
            this.nounB = nounB;
            this.sapVertex = -1;
        }

        public SapVertexForNouns(String nounA, String nounB, int sapVertex) {
            this.nounA = nounA;
            this.nounB = nounB;
            this.sapVertex = sapVertex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SapVertexForNouns that = (SapVertexForNouns) o;
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

        final In synsetsIn = new In(synsets);
        while (synsetsIn.hasNextLine()) {
            String line = synsetsIn.readLine();
            String[] tokens = line.split(",");
            int synsetId = Integer.parseInt(tokens[0]);
            final Set<String> nounsOfCurSynset = Arrays.stream(tokens[1].split("\\s+")).collect(Collectors.toSet());
            nouns.addAll(nounsOfCurSynset);
            synsetIdToNouns.put(synsetId, nounsOfCurSynset);
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
        }
        sap = new SAP(digraph);
        distanceForNounsMap = new HashMap<>();
        sapVertexForNounsMap = new HashMap<>();
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

    private void checkNounsNotNull(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Arguments tp distance must not be null!");
        }
    }

    private Iterable<Integer> getSynsetsWithNoun(String noun) {
        return synsetIdToNouns.entrySet().stream().filter(entry -> entry.getValue().contains(noun))
                .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        DistanceForNouns distanceForNouns = distanceForNounsMap.computeIfAbsent(new DistanceForNouns(nounA, nounB),
                key -> new DistanceForNouns(nounA, nounB, sap.length(getSynsetsWithNoun(nounA), getSynsetsWithNoun(nounB))));
        distanceForNounsMap.put(new DistanceForNouns(nounB, nounA), distanceForNouns);
        int result = distanceForNouns.distance;
        if (result != -1) {
            return result;
        } else {
            throw new IllegalArgumentException(String.format("distance between %s and %s can not be calculated", nounA, nounB));
        }
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        SapVertexForNouns sapVertexForNouns = sapVertexForNounsMap.computeIfAbsent(new SapVertexForNouns(nounA, nounB),
                key -> new SapVertexForNouns(nounA, nounB, sap.ancestor(getSynsetsWithNoun(nounA), getSynsetsWithNoun(nounB))));
        sapVertexForNounsMap.put(new SapVertexForNouns(nounB, nounA), sapVertexForNouns);
        final int sapVertex = sapVertexForNouns.sapVertex;
        if (sapVertex != -1) {
            return String.join(" ", synsetIdToNouns.get(sapVertex));
        } else {
            throw new IllegalArgumentException(String.format("sap between %s and %s can not be calculated", nounA, nounB));
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        final WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.printf("nouns: %s%n", String.join(", ", wordNet.nouns()));
        String someNoun = "banana";
        System.out.printf("%s is noun: %b%n", someNoun, wordNet.isNoun(someNoun));
        String nounA = "table";
        String nounB = "horse";
        System.out.println(wordNet.distance(nounA, nounB));
        System.out.println(wordNet.sap(nounA, nounB));
    }
}
