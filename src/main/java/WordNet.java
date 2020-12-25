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

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Constructor arguments must not be null!");
        }

        Map<Integer, String> synsetIdToName = new HashMap<>();

        final In synsetsIn = new In(synsets);
        while (synsetsIn.hasNextLine()) {
            String line = synsetsIn.readLine();
            String[] tokens = line.split(",");
            int synsetId = Integer.parseInt(tokens[0]);
            String synsetName = tokens[1];
            synsetIdToName.put(synsetId, synsetName);
        }

        digraph = new Digraph(synsetIdToName.size());

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
        if ((synsetIdToName.size() - nonRootSynsets.size() > 1) || directedCycle.hasCycle()) {
            throw new IllegalArgumentException("digraph does not have single root");
        } else {
            System.out.println("Digraph is acyclic and has single root.");
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return null;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument to isNoun must not be null!");
        }
        return false;
    }

    private void checkNounsNotNull(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Arguments tp distance must not be null!");
        }
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        return -1;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNounsNotNull(nounA, nounB);
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        final WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
    }
}