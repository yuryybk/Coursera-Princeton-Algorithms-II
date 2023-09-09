import java.util.Arrays;

public class CircularSuffixArray {
    private final String src;

    private final CircularSuffix[] suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        src = s;
        suffixes = new CircularSuffix[s.length()];
        for (int i = 0; i < s.length(); i++) {
            suffixes[i] = new CircularSuffix(src, i);
        }

        Arrays.sort(suffixes);
    }

    // length of s
    public int length() {
        return src.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= src.length()) {
            throw new IllegalArgumentException();
        }
        return suffixes[i].getOffset();
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        if (csa.index(3) != 0) {
            throw new IllegalStateException("Incorrect index");
        }
        int[] order = {11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2};
        for (int i = 0; i < order.length; i++) {
            if (csa.index(i) != order[i]) {
                throw new IllegalStateException("Incorrect index");
            }
        }
    }

    private static class CircularSuffix implements Comparable<CircularSuffix> {

        private final String str;

        private final int offset;

        public CircularSuffix(String str, int offset) {
            this.str = str;
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }

        private char charAt(int i) {
            int pos = offset + i;
            if (pos >= str.length()) {
                pos = pos - str.length();
            }
            return str.charAt(pos);
        }

        @Override
        public int compareTo(CircularSuffix other) {
            for (int i = 0; i < str.length(); i++) {
                if (charAt(i) > other.charAt(i)) {
                    return 1;
                } else if (charAt(i) < other.charAt(i)) {
                    return -1;
                }
            }
            return 0;
        }
    }
}
