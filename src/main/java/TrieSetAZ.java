public class TrieSetAZ {
    // English alphabet
    private static final int R = 26;

    private Node root;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    private static class Node {
        private final Node[] next = new Node[R];
        private boolean isString;
    }

    /**
     * Initializes an empty set of strings.
     */
    public TrieSetAZ() {
    }

    private static int charAt(String key, int d) {
        return (int) key.charAt(d) - 65;
    }

    public boolean containsPrefix(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("argument to getNodeWithPrefix() is null");
        Node node = get(root, prefix, 0);
        return node != null;
    }

    /**
     * Does the set contain the given key?
     *
     * @param key the key
     * @return {@code true} if the set contains {@code key} and
     * {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        int c = charAt(key, d);
        return get(x.next[c], key, d + 1);
    }

    /**
     * Adds the key to the set if it is not already present.
     *
     * @param key the key to add
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (!x.isString) n++;
            x.isString = true;
        } else {
            int c = charAt(key, d);
            x.next[c] = add(x.next[c], key, d + 1);
        }
        return x;
    }

    /**
     * Returns the number of strings in the set.
     *
     * @return the number of strings in the set
     */
    public int size() {
        return n;
    }

    /**
     * Is the set empty?
     *
     * @return {@code true} if the set is empty, and {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

}
