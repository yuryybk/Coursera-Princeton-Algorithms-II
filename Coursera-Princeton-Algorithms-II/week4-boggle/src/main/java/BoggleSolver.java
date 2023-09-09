import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private final BoggleTrieST trie = new BoggleTrieST();

    private Set<String> words = new HashSet<>();

    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            trie.put(word, true);
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words = new HashSet<>();
        boolean[][] marked = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int column = 0; column < board.cols(); column++) {
                findWord("", row, column, board, marked);
            }
        }
        return words;
    }

    private void findWord(String prefix, int row, int col, BoggleBoard board, boolean[][] marked) {
        if (!isValid(row, col, board)) {
            return;
        }

        if (marked[row][col]) {
            return;
        }

        char nextLetter = board.getLetter(row, col);
        prefix = prefix + nextLetter;
        if (nextLetter == 'Q') {
            prefix = prefix + 'U';
        }


        BoggleTrieST.SearchResult searchResult = trie.get(prefix);
        if (prefix.length() >= 3 && !searchResult.hasNode()) {
            return;
        }

        marked[row][col] = true;
        if (prefix.length() >= 3 && searchResult.isWord()) {
            words.add(prefix);
        }

        findWord(prefix, row - 1, col, board, marked);
        findWord(prefix, row - 1, col + 1, board, marked);
        findWord(prefix, row, col + 1, board, marked);
        findWord(prefix, row + 1, col + 1, board, marked);
        findWord(prefix, row + 1, col, board, marked);
        findWord(prefix, row + 1, col - 1, board, marked);
        findWord(prefix, row, col - 1, board, marked);
        findWord(prefix, row - 1, col - 1, board, marked);

        marked[row][col] = false;
    }

    private boolean isValid(int row, int column, BoggleBoard board) {
        return row >= 0 && row < board.rows() && column >= 0 && column < board.cols();
    }

    public int scoreOf(String word) {
        BoggleTrieST.SearchResult searchResult = trie.get(word);
        if (searchResult.isWord()) {
            switch (word.length()) {
                case 0:
                case 1:
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}