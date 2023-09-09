import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.HashSet;
import java.util.Set;

public class DistancesUtil {

    public static Distances findAllDistances(Digraph digraph, int v) {
        Set<Integer> vertices = new HashSet<>();
        vertices.add(v);
        return findAllDistances(digraph, vertices);
    }

    public static Distances findAllDistances(Digraph digraph, Iterable<Integer> vertices) {
        boolean[] marked = new boolean[digraph.V()];
        int[] dist = new int[digraph.V()];
        Queue<Integer> queue = new Queue<>();
        for (Integer v: vertices) {
            if (v == null || v >= digraph.V() || v < 0) {
                throw new IllegalArgumentException();
            }
            queue.enqueue(v);
            marked[v] = true;
        }
        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            for (int w : digraph.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    dist[w] = dist[v] + 1;
                    queue.enqueue(w);
                }
            }
        }
        return new Distances(dist, marked);
    }

    public static SapInfo sapDistanceWithAncestor(Digraph digraph, Distances distV, Distances distW) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < digraph.V(); i++) {
            // Checking if vertex was reached
            if (distV.getMarked()[i] && distW.getMarked()[i]) {
                int dist = distV.getDistances()[i] + distW.getDistances()[i];
                if (dist < min) {
                    min = dist;
                    minIndex = i;
                }
            }
        }

        SapInfo sapInfo;
        if (min == Integer.MAX_VALUE) {
            sapInfo = new SapInfo(-1, -1);
        } else {
            sapInfo = new SapInfo(min, minIndex);
        }
        return sapInfo;
    }
}
