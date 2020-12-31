import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WordNet {

    private final Digraph digraph;
    private final Map<Integer, Set<String>> synsetIdToNouns;
    private final Map<String, Set<Integer>> nounToSynsetIds;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Constructor arguments must not be null!");
        }

        synsetIdToNouns = new HashMap<>();
        nounToSynsetIds = new HashMap<>();

        final In synsetsIn = new In(synsets);
        while (synsetsIn.hasNextLine()) {
            String line = synsetsIn.readLine();
            String[] tokens = line.split(",");
            int synsetId = Integer.parseInt(tokens[0]);
            final Set<String> nounsOfCurSynset = Arrays.stream(tokens[1].split("\\s+")).collect(Collectors.toSet());
            for (String noun : nounsOfCurSynset) {
                nounToSynsetIds.computeIfAbsent(noun, key -> new HashSet<>()).add(synsetId);
            }
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
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToSynsetIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument to isNoun must not be null!");
        }
        return nounToSynsetIds.containsKey(word);
    }

    private void checkNounsNotNull(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Arguments tp distance must not be null!");
        }
    }

    private Iterable<Integer> getSynsetsWithNoun(String noun) {
        return nounToSynsetIds.get(noun);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        int result = sap.length(getSynsetsWithNoun(nounA), getSynsetsWithNoun(nounB));
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
        final int sapVertex = sap.ancestor(getSynsetsWithNoun(nounA), getSynsetsWithNoun(nounB));
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
