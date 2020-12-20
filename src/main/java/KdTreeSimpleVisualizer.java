import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

public class KdTreeSimpleVisualizer {

    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        KdTree kdtree = new KdTree();
//        kdtree.insert(new Point2D(0.7, 0.2));
//        kdtree.insert(new Point2D(0.5, 0.4));
//        kdtree.insert(new Point2D(0.2, 0.3));
//        kdtree.insert(new Point2D(0.4, 0.7));
//        kdtree.insert(new Point2D(0.9, 0.6));

//        kdtree.insert(new Point2D(0.625, 0.625));
//        System.out.println("Size: " + kdtree.size());
//        kdtree.insert(new Point2D(0.6250, 0.75));
//        System.out.println("Size: " + kdtree.size());

        kdtree.insert(new Point2D(0.7, 0.2));
        kdtree.insert(new Point2D(0.5, 0.4));
        kdtree.insert(new Point2D(0.2, 0.3));
        kdtree.insert(new Point2D(0.4, 0.7));
        kdtree.insert(new Point2D(0.9, 0.6));

//        kdtree.insert(new Point2D(0.158530, 0.4869010));
//        System.out.println("Size: " + kdtree.size());
//        kdtree.insert(new Point2D(0.792202, 0.7628250));
//        System.out.println("Size: " + kdtree.size());
//        kdtree.insert(new Point2D(0.738013, 0.8276160));
//        System.out.println("Size: " + kdtree.size());
//        kdtree.insert(new Point2D(0.615232, 0.0644540));
//        System.out.println("Size: " + kdtree.size());
//        kdtree.insert(new Point2D(0.107092, 0.8633170));
//        System.out.println("Size: " + kdtree.size());
        StdDraw.clear();
        kdtree.draw();
//
//
        Point2D p = new Point2D(0.681, 0.752);
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.MAGENTA);
        p.draw();
        Point2D nearestPoint = kdtree.nearest(p);
        System.out.println("nearestPoint: " + nearestPoint);
        System.out.println("distance to nearest: " + p.distanceSquaredTo(nearestPoint));
        System.out.println("distance to (0.9, 0.6)): " + p.distanceSquaredTo(new Point2D(0.9, 0.6)));
        StdDraw.setPenRadius();
        p.drawTo(nearestPoint);
        StdDraw.show();
    }
}
