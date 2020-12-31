import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private final Picture picture;
    private final double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture argument must not be null!");
        }

        this.picture = new Picture(picture);
        this.energy = new double[picture.height()][picture.width()];
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

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException("x or y are outside of expected range!");
        }

        if (isOnBorder(x, y)) {
            return 1000;
        }

        final Color color = picture.get(x, y);
        color.get

        return -1;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return null;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return null;
    }

    private void checkSeamNotNull(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam argument must be not null!");
        }
    }

    private void checkSeamIsConsecutive(int[] seam) {
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i-1]) > 1 ) {
                throw new IllegalArgumentException(
                        String.format("Value for [%d] is %d, value for [%d] is %d. Absolute difference is more than 1"
                                , i-1, seam[i-1], i, seam[i]));
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
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

}