
public class Distances {

    private final int[] distances;
    private final boolean[] marked;

    Distances(int[] d, boolean[] m) {
        this.distances = d;
        this.marked = m;
    }

    public int[] getDistances() {
        return distances;
    }

    public boolean[] getMarked() {
        return marked;
    }
}
