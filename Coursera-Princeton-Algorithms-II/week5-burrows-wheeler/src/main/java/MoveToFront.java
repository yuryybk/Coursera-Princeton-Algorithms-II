import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] sequence = initSequence();
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            int index = -1;
            for (int i = 0; i < R; i++) {
                if (sequence[i] == ch) {
                    index = i;
                    BinaryStdOut.write((char) i);
                    break;
                }
            }
            for (int j = index; j > 0; j--) {
                sequence[j] = sequence[j - 1];
            }
            sequence[0] = ch;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] sequence = initSequence();
        while (!BinaryStdIn.isEmpty()) {
            char index = BinaryStdIn.readChar();
            char ch = sequence[index];
            BinaryStdOut.write(ch);
            for (int j = index; j > 0; j--) {
                sequence[j] = sequence[j - 1];
            }
            sequence[0] = ch;
        }
        BinaryStdOut.close();
    }

    private static char[] initSequence() {
        char[] sequence = new char[R];
        for (int i = 0; i < R; i++) {
            sequence[i] = (char) i;
        }
        return sequence;
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        }
    }

}
