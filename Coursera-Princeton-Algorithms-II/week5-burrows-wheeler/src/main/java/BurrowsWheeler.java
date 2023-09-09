import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.BinaryStdIn;
public class BurrowsWheeler {

    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int firstIndex = csa.index(0);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                firstIndex = i;
                break;
            }
        }
        BinaryStdOut.write(firstIndex);
        for (int i = 0; i < csa.length(); i++) {

            // Need last index of the sorted string
            int charIndex = csa.index(i) - 1;
            if (charIndex == -1) {
                charIndex = s.length() - 1;
            }
            BinaryStdOut.write(s.charAt(charIndex));
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int firstIndex = 0;
        String transformed = null;
        while (!BinaryStdIn.isEmpty()) {
            firstIndex = BinaryStdIn.readInt();
            transformed = BinaryStdIn.readString();
        }

        if (transformed == null) {
            throw new IllegalStateException();
        }

        char[] chars = transformed.toCharArray();
        int[] nextTo = radixSortWithNextToArray(chars);

        int nextCharIdx = firstIndex;
        for (int i = 0; i < transformed.length(); i++) {
            BinaryStdOut.write(chars[nextCharIdx]);
            nextCharIdx = nextTo[nextCharIdx];
        }

        BinaryStdOut.close();
    }

    private static int[] radixSortWithNextToArray(char[] srcChars) {
        int[] count = new int[R + 1];
        char[] aux = new char[srcChars.length];
        int[] nextTo = new int[srcChars.length];

        for (char srcChar : srcChars) {
            count[srcChar + 1]++;
        }

        for (int r = 0; r < R; r++) {
            count[r + 1] += count[r];
        }

        for (int i = 0; i < srcChars.length; i++) {

            // Get index of the char in the aux (sorted) array
            int auxIdx = count[srcChars[i]];

            // Put current char into sorted array
            aux[auxIdx] = srcChars[i];

            // Save i-position of the source char into mapping (sorted idx -> source idx)
            nextTo[auxIdx] = i;

            // Increase pointer to aux for the current char into count array
            count[srcChars[i]]++;
        }

        for (int i = 0; i < srcChars.length; i++) {
            srcChars[i] = aux[i];
        }

        return nextTo;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
