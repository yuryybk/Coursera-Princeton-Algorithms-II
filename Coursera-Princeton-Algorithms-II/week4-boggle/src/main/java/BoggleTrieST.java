public class BoggleTrieST {
    private static final int R = 26;
    private static final char START_LETTER_INDEX = 'A';

    private Node root;

    // R-way trie node
    private static class Node {
        private boolean isWord;
        private final Node[] next = new Node[R];
    }

    public static class SearchResult {
        private boolean isWord = false;
        private boolean hasNode = true;

        public boolean hasNode() {
            return hasNode;
        }

        public boolean isWord() {
            return isWord;
        }
    }

    public BoggleTrieST() {
    }

    public SearchResult get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node node = get(root, key, 0);
        SearchResult result = new SearchResult();
        if (node == null) {
            result.hasNode = false;
            result.isWord = false;
        } else {
            result.isWord = node.isWord;
            result.hasNode = true;
        }
        return result;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - START_LETTER_INDEX], key, d + 1);
    }

    public void put(String key, boolean isWord) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        else root = put(root, key, isWord, 0);
    }

    private Node put(Node x, String key, boolean isWord, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.isWord = isWord;
            return x;
        }
        char c = key.charAt(d);
        x.next[c - START_LETTER_INDEX] = put(x.next[c - START_LETTER_INDEX], key, isWord, d + 1);
        return x;
    }
}