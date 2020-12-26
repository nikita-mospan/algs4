import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("wordnet argument must not be null!");
        }
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("nouns argument must not be null!");
        }
        String outcast = null;
        int maxDistance = 0;
        for (int i = 0; i < nouns.length; i++) {
            int curDistance = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i != j) {
                    curDistance += wordNet.distance(nouns[i], nouns[j]);
                }
            }
            if (curDistance > maxDistance) {
                maxDistance = curDistance;
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
