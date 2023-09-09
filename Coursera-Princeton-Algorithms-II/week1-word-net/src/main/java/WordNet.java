import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    private final Digraph digraph;
    private final Map<String, List<Integer>> synsetNouns = new HashMap<>();
    private final List<String> sysnsets = new ArrayList<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsetsFile, String hypernymsFile) {
        int count = readSynsets(synsetsFile);
        digraph = new Digraph(count);
        readHypernyms(hypernymsFile);
        checkTopologicalOrder();
        checkRoots();
        sap = new SAP(digraph);
    }

    private void checkTopologicalOrder() {
        Topological topological = new Topological(digraph);
        if (!topological.hasOrder()) {
            throw new IllegalArgumentException();
        }
    }

    private void checkRoots() {
        int roots = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) {
                roots++;
                if (roots >= 2) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    private int readSynsets(String synsetsFile) {
        In in = null;
        try {
            in = new In(synsetsFile);
            while (in.hasNextLine()) {
                String currentLine = in.readLine();
                String[] items = currentLine.split(",");
                sysnsets.add(items[1]);
                String[] nouns = items[1].split(" ");
                for (String noun : nouns) {
                    Integer vertexIndex = Integer.valueOf(items[0]);
                    List<Integer> indices = synsetNouns.get(noun);
                    if (indices == null) {
                        indices = new ArrayList<>();
                        indices.add(vertexIndex);
                        synsetNouns.put(noun, indices);
                    } else {
                        indices.add(vertexIndex);
                    }
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return sysnsets.size();
    }

    private void readHypernyms(String hypernymsFile) {
        In in = null;
        try {
            in = new In(hypernymsFile);
            while (in.hasNextLine()) {
                String currentLine = in.readLine();
                String[] items = currentLine.split(",");
                for (int i = 1; i < items.length; i++) {
                    digraph.addEdge(Integer.parseInt(items[0]), Integer.parseInt(items[i]));
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetNouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return synsetNouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNouns(nounA, nounB);
        return sap.length(synsetNouns.get(nounA), synsetNouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNouns(nounA, nounB);
        int ancestor = sap.ancestor(synsetNouns.get(nounA), synsetNouns.get(nounB));
        if (ancestor < sysnsets.size()) {
            return sysnsets.get(ancestor);
        } else {
            return null;
        }
    }

    private void checkNouns(String nounA, String nounB) {
        if (nounA == null || !isNoun(nounA) || nounB == null || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {

        WordNet wordNet = new WordNet("assets/week1-word-net/synsets.txt", "assets/week1-word-net/hypernyms.txt");
        int distance = wordNet.distance("AND_circuit", "gate");
        if (distance != 1) {
            throw new IllegalStateException("Distance between \"AND_circuit\" and \"gate\" should be 1 but " + distance);
        }

        distance = wordNet.distance("entity", "thing");
        if (distance != 1) {
            throw new IllegalStateException("SAP between \"entity\" and \"thing\" should be \"1\" but " + distance);
        }

        distance = wordNet.distance("appellation", "going");
        if (distance != 9) {
            throw new IllegalStateException("SAP between \"entity\" and \"thing\" should be \"1\" but " + 2);
        }

        String sap = wordNet.sap("AND_circuit", "gate");
        if (!sap.equals("gate logic_gate")) {
            throw new IllegalStateException("SAP between \"AND_circuit\" and \"gate\" should be \"gate logic_gate\" but " + sap);
        }

        StdOut.println("Tests passed!");
    }
}
