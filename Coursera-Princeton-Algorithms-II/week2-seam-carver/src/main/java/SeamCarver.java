import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private static final int MASK = 0xFF;
    private double[][] energy;
    private int[][] picture;
    private boolean isVertical = true;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture is null");
        }

        int height = picture.height();
        int width = picture.width();
        this.picture = new int[height][width];
        copyPicture(picture);

        energy = new double[height][width];
        calculateEnergyMatrix(height, width);
    }

    private void copyPicture(Picture p) {
        for (int col = 0; col < p.width(); col++) {
            for (int row = 0; row < p.height(); row++) {
                this.picture[row][col] = p.getRGB(col, row);
            }
        }
    }

    private void calculateEnergyMatrix(int height, int width) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    energy[y][x] = 1000;
                } else {
                    calculateEnergyCell(y, x);
                }
            }
        }
    }

    private void calculateEnergyCell(int y, int x) {
        double gradXSquared = calculateSquaredGradient(picture[y][x - 1], picture[y][x + 1]);
        double gradYSquared = calculateSquaredGradient(picture[y - 1][x], picture[y + 1][x]);
        energy[y][x] = Math.sqrt(gradXSquared + gradYSquared);
    }

    private double calculateSquaredGradient(int colorNext, int colorPrev) {
        return Math.pow(getRed(colorNext) - getRed(colorPrev), 2)
                + Math.pow(getGreen(colorNext) - getGreen(colorPrev), 2)
                + Math.pow(getBlue(colorNext) - getBlue(colorPrev), 2);
    }

    private int getRed(int color) {
        return color >> 16 & MASK;
    }

    private int getGreen(int color) {
        return color >> 8 & MASK;
    }

    private int getBlue(int color) {
        return color & MASK;
    }

    // current picture
    public Picture picture() {
        if (!isVertical) {
            transposeAndRecalculate();
            isVertical = true;
        }
        Picture newPicture = new Picture(width(), height());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                int rgb = picture[row][col];
                newPicture.setRGB(col, row, rgb);
            }
        }
        return newPicture;
    }

    // width of current picture
    public int width() {
        if (isVertical) {
            return picture[0].length;
        } else {
            return picture.length;
        }
    }

    // height of current picture
    public int height() {
        if (isVertical) {
            return picture.length;
        } else {
            return picture[0].length;
        }
    }

    private int widthNow() {
        return picture[0].length;
    }

    private int heightNow() {
        return picture.length;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        checkCoordinates(x, y);
        if (!isVertical) {
            transposeAndRecalculate();
            isVertical = true;
        }
        return energy[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findVerticalSeam(false);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findVerticalSeam(true);
    }

    private int[] findVerticalSeam(boolean useVertical) {
        if (useVertical != isVertical) {
            transposeAndRecalculate();
            isVertical = useVertical;
        }

        int height = heightNow();
        int width = widthNow();
        double[][] distTo = new double[height][width];
        int[][] edgeTo = new int[height][width];

        initGraphArrays(height, width, distTo, edgeTo);
        calculateDistances(height, width, distTo, edgeTo);
        int endMinEnergyColumnIdx = findLastMinEnergyColumn(height, width, distTo);

        if (endMinEnergyColumnIdx >= width || endMinEnergyColumnIdx == -1) {
            throw new IllegalStateException("Min energy column index = " + endMinEnergyColumnIdx);
        }

        return findVerticalSeam(edgeTo, endMinEnergyColumnIdx);
    }

    private void initGraphArrays(int height, int width, double[][] distTo, int[][] edgeTo) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0) {
                    distTo[y][x] = 1000;
                } else {
                    distTo[y][x] = Double.POSITIVE_INFINITY;
                }
                edgeTo[y][x] = -1;
            }
        }
    }

    private void calculateDistances(int height, int width, double[][] distTo, int[][] edgeTo) {
        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                relaxNeighbours(y, x, distTo, edgeTo);
            }
        }
    }

    private void relaxNeighbours(int y, int x, double[][] distTo, int[][] edgeTo) {
        relax(y, x, y - 1, x - 1, distTo, edgeTo);
        relax(y, x, y - 1, x, distTo, edgeTo);
        relax(y, x, y - 1, x + 1, distTo, edgeTo);
    }

    private void relax(int yTo, int xTo, int y, int x, double[][] distTo, int[][] edgeTo) {
        if (x >= 0 && x < widthNow() && y >= 0 && y < heightNow()) {
            double distFromCurrent = distTo[y][x] + energy[yTo][xTo];
            if (distFromCurrent < distTo[yTo][xTo]) {
                distTo[yTo][xTo] = distFromCurrent;
                edgeTo[yTo][xTo] = x;
            }
        }
    }

    private int findLastMinEnergyColumn(int height, int width, double[][] distTo) {
        double min = Integer.MAX_VALUE;
        int minIdx = -1;
        for (int x = 0; x < width; x++) {
            if (distTo[height - 1][x] < min) {
                minIdx = x;
                min = distTo[height - 1][x];
            }
        }
        return minIdx;
    }

    private int[] findVerticalSeam(int[][] edgeTo, int endMinEnergyColumnIdx) {
        int[] seam = new int[picture.length];
        int prevMinEnergyColumnIdx = endMinEnergyColumnIdx;
        seam[picture.length - 1] = prevMinEnergyColumnIdx;
        for (int y = picture.length - 2; y >= 0; y--) {
            seam[y] = edgeTo[y + 1][prevMinEnergyColumnIdx];
            prevMinEnergyColumnIdx = edgeTo[y + 1][prevMinEnergyColumnIdx];
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam, false);
        removeVerticalSeam(seam, false);
        isVertical = false;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam, true);
        removeVerticalSeam(seam, true);
        isVertical = true;
    }

    private void removeVerticalSeam(int[] seam, boolean useVertical) {
        if (useVertical != isVertical) {
            transposeAndRecalculate();
            isVertical = true;
        }
        int height = heightNow();
        int width = widthNow();

        double[][] newEnergy = new double[height][width - 1];
        int[][] newPicture = new int[height][width - 1];
        for (int y = 0; y < height; y++) {
            int x = seam[y];
            System.arraycopy(energy[y], 0, newEnergy[y], 0, x);
            System.arraycopy(energy[y], x + 1, newEnergy[y], x, width - x - 1);

            System.arraycopy(picture[y], 0, newPicture[y], 0, x);
            System.arraycopy(picture[y], x + 1, newPicture[y], x, width - x - 1);
        }
        energy = newEnergy;
        picture = newPicture;

        calculateEnergyMatrix(heightNow(), widthNow());
    }

    private void transposeAndRecalculate() {
        transposePicture();
        recalculateEnergyMatrix();
    }

    private void transposePicture() {
        int width = widthNow();
        int height = heightNow();
        int[][] newPicture = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newPicture[x][y] = picture[y][x];
            }
        }
        picture = newPicture;
    }

    private void recalculateEnergyMatrix() {
        energy = new double[heightNow()][widthNow()];
        calculateEnergyMatrix(heightNow(), widthNow());
    }

    private void checkCoordinates(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException("Bad x = " + x + "or y = " + y);
        }
    }

    private void checkSeam(int[] seam, boolean useVertical) {
        if (seam == null) {
            throw new IllegalArgumentException("Seam is null");
        }

        if (useVertical && width() == 1) {
            throw new IllegalArgumentException("Can't remove last column");
        }

        if (!useVertical && height() == 1) {
            throw new IllegalArgumentException("Can't remove last row");
        }

        if (useVertical && seam.length != height()) {
            throw new IllegalArgumentException("Seam length should be equal to height");
        }

        if (!useVertical && seam.length != width()) {
            throw new IllegalArgumentException("Seam length should be equal to height");
        }

        for (int i = 0; i < seam.length - 1; i++) {
            if (seam[i] - seam[i+1] > 1 || seam[i] - seam[i+1] < -1) {
                throw new IllegalArgumentException("Wrong seam indices");
            }
        }

        for (int j : seam) {
            if (j < 0 || (j >= height() && !useVertical) || (j >= width() && useVertical)) {
                throw new IllegalArgumentException("Wrong seam indices");
            }
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // Not used
    }
}