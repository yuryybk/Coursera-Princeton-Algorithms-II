public class SapInfo {

    private final int ancestor;
    private final int distance;

    public SapInfo(int distance, int ancestor) {
        this.ancestor = ancestor;
        this.distance = distance;
    }

    public int getAncestor() {
        return ancestor;
    }

    public int getDistance() {
        return distance;
    }
}
