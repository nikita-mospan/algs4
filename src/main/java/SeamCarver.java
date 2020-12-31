import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private final Picture picture;
    private final double[][] energy;
    private final double[][] energyTo;
    private final int[][] edgeTo;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture argument must not be null!");
        }

        this.picture = new Picture(picture);
        this.energy = new double[height()][width()];

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energy[y][x] = energy(x, y);
            }
        }

        this.energyTo = new double[height()][width()];
        this.edgeTo = new int[height()][width()];

    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    private boolean isOnBorder(int x, int y) {
        return x == 0 || x == width() - 1 || y == 0 || y == height() - 1;
    }

    private int getSquareDelta(Color c1, Color c2) {
        final int diffRed = c1.getRed() - c2.getRed();
        final int diffGreen = c1.getGreen() - c2.getGreen();
        final int diffBlue = c1.getBlue() - c2.getBlue();

        return diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue;
    }

    private void checkXAndYWithinRange(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException("x or y are outside of expected range!");
        }
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        checkXAndYWithinRange(x, y);

        //y is row index and x is column index
        if (energy[y][x] > 0) {
            return energy[y][x];
        }

        double result;
        if (isOnBorder(x, y)) {
            result = 1000;
        } else {
            final Color leftColor = picture.get(x-1, y);
            final Color rightColor = picture.get(x+1, y);
            final Color upColor = picture.get(x, y-1);
            final Color downColor = picture.get(x, y+1);

            int squaredDeltaX = getSquareDelta(leftColor, rightColor);
            int squaredDeltaY = getSquareDelta(upColor, downColor);

            result = Math.sqrt(squaredDeltaX + squaredDeltaY);
        }

        return result;
    }

    private void relax(int xSrc, int ySrc, int destCoordinate, boolean relaxVertical) {
        int xDest;
        int yDest;
        if (relaxVertical) {
            xDest = destCoordinate;
            yDest = ySrc + 1;
        } else {
            xDest = xSrc + 1;
            yDest = destCoordinate;
        }
        try {
            checkXAndYWithinRange(xDest, yDest);
        } catch (IllegalArgumentException e) {
            return;
        }

        if (energyTo[yDest][xDest] > energyTo[ySrc][xSrc] + energy[ySrc][xSrc]) {
            energyTo[yDest][xDest] = energyTo[ySrc][xSrc] + energy[ySrc][xSrc];
            if (relaxVertical) {
                edgeTo[yDest][xDest] = xSrc;
            } else {
                edgeTo[yDest][xDest] = ySrc;
            }
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                energyTo[y][x] = Double.POSITIVE_INFINITY;
            }
        }
        for (int y = 0; y < height(); y++) {
            energyTo[y][0] = 0;
        }

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height() - 1; y++) {
                relax(x, y, y - 1, false);
                relax(x, y, y, false);
                relax(x, y, y + 1, false);
            }
        }

        double minEnergyOnRight = Double.POSITIVE_INFINITY;
        int yWithMinEnergyOnRight = -1;
        for (int y = 0; y < height(); y++) {
            if (energyTo[y][width() - 1] < minEnergyOnRight) {
                yWithMinEnergyOnRight = y;
                minEnergyOnRight = energyTo[y][width() - 1];
            }
        }

        int[] result = new int[width()];
        int y = yWithMinEnergyOnRight;
        for (int x = width() - 1; x >= 0; x--) {
            result[x] = y;
            y = edgeTo[y][x];
        }

        return result;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energyTo[y][x] = Double.POSITIVE_INFINITY;
            }
        }
        for (int x = 0; x < width(); x++) {
            energyTo[0][x] = 0;
        }

        for (int y = 0; y < height() - 1; y++) {
            for (int x = 0; x < width(); x++) {
                relax(x, y, x - 1, true);
                relax(x, y, x, true);
                relax(x, y, x + 1, true);
            }
        }

        double minEnergyOnBottom = Double.POSITIVE_INFINITY;
        int xWithMinEnergyOnBottom = -1;
        for (int x = 0; x < width(); x++) {
            if (energyTo[height() - 1][x] < minEnergyOnBottom) {
                xWithMinEnergyOnBottom = x;
                minEnergyOnBottom = energyTo[height() - 1][x];
            }
        }

        int[] result = new int[height()];
        int x = xWithMinEnergyOnBottom;
        for (int y = height() - 1; y >= 0; y--) {
            result[y] = x;
            x = edgeTo[y][x];
        }

        return result;
    }

    private void checkSeamNotNull(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam argument must be not null!");
        }
    }

    private void checkSeamIsConsecutive(int[] seam) {
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException(
                        String.format("Value for [%d] is %d, value for [%d] is %d. Absolute difference is more than 1"
                                , i - 1, seam[i - 1], i, seam[i]));
            }
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkSeamNotNull(seam);
        if (height() <= 1) {
            throw new IllegalArgumentException("Can not remove horizontal seam when height is 1 or less");
        }
        if (seam.length != width()) {
            throw new IllegalArgumentException(String.format("Seam width must be %d but got %d", width(), seam.length));
        }
        checkSeamIsConsecutive(seam);



    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkSeamNotNull(seam);
        if (width() <= 1) {
            throw new IllegalArgumentException("Can not remove vertical seam when width is 1 or less");
        }
        if (seam.length != height()) {
            throw new IllegalArgumentException(String.format("Seam height must be %d but got %d", height(), seam.length));
        }
        checkSeamIsConsecutive(seam);

//        for (int y = 0; y < height(); y++) {
//            picture.
//        }



    }

    //  unit testing (optional)
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

}