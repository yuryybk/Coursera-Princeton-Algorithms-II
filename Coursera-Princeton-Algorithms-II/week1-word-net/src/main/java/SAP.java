import edu.princeton.cs.algs4.Digraph;

public class SAP {

    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        digraph = new Digraph(G.V());
        for (int v = 0; v < G.V(); v++) {
            for (Integer w : G.adj(v)) {
                digraph.addEdge(v, w);
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v >= digraph.V() || w >= digraph.V()) {
            throw new IllegalArgumentException();
        }
        return findSap(v, w).getDistance();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v >= digraph.V() || w >= digraph.V()) {
            throw new IllegalArgumentException();
        }
        return findSap(v, w).getAncestor();
    }

    private SapInfo findSap(int v, int w) {
        if (v >= digraph.V() || w >= digraph.V()) {
            throw new IllegalArgumentException();
        }
        Distances distV = DistancesUtil.findAllDistances(digraph, v);
        Distances distW = DistancesUtil.findAllDistances(digraph, w);
        return DistancesUtil.sapDistanceWithAncestor(digraph, distV, distW);
    }


    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return findSap(v, w).getDistance();
    }

    // a common ancestor that participates in the shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return findSap(v, w).getAncestor();
    }

    private SapInfo findSap(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        Distances distV = DistancesUtil.findAllDistances(digraph, v);
        Distances distW = DistancesUtil.findAllDistances(digraph, w);
        return DistancesUtil.sapDistanceWithAncestor(digraph, distV, distW);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
