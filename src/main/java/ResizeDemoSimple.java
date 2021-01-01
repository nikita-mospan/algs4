import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class ResizeDemoSimple {

    private static final boolean HORIZONTAL   = true;
    private static final boolean VERTICAL     = false;

    private static void printSeam(SeamCarver carver, int[] seam, boolean direction) {
        double totalSeamEnergy = 0.0;

        for (int row = 0; row < carver.height(); row++) {
            for (int col = 0; col < carver.width(); col++) {
                double energy = carver.energy(col, row);
                String marker = " ";
                if ((direction == HORIZONTAL && row == seam[col]) ||
                        (direction == VERTICAL   && col == seam[row])) {
                    marker = "*";
                    totalSeamEnergy += energy;
                }
                StdOut.printf("%7.2f%s ", energy, marker);
            }
            StdOut.println();
        }
        // StdOut.println();
        StdOut.printf("Total energy = %f\n", totalSeamEnergy);
        StdOut.println();
        StdOut.println();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            StdOut.println("Usage:\njava ResizeDemoSimple [image filename] [num cols to remove] [num rows to remove]");
            return;
        }

        Picture inputImg = new Picture(args[0]);
        int removeColumns = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]);

        StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        SeamCarver sc = new SeamCarver(inputImg);
        System.out.println("Image before removing");
        System.out.println(inputImg);

        for (int i = 0; i < removeColumns; i++) {
            int[] verticalSeam = sc.findVerticalSeam();
            for (int x : verticalSeam)
                StdOut.print(x + " ");
            StdOut.println("}");
            printSeam(sc, verticalSeam, VERTICAL);

            sc.removeVerticalSeam(verticalSeam);
        }

        for (int i = 0; i < removeRows; i++) {
            int[] horizontalSeam = sc.findHorizontalSeam();
            for (int y : horizontalSeam)
                StdOut.print(y + " ");
            StdOut.println("}");
            printSeam(sc, horizontalSeam, HORIZONTAL);
            sc.removeHorizontalSeam(horizontalSeam);
        }

        Picture outputImg = sc.picture();

        System.out.println("Image after removing");
        System.out.println(outputImg);
        System.out.println("Energy after removal from scratch");
        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%9.2f ", sc.energy(col, row));
            StdOut.println();
        }

        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());

    }

}
