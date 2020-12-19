import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Set;
import java.util.TreeSet;

public class PointSET {

    private final Set<Point2D> point2DSet;

    public PointSET()                               // construct an empty set of points
    {
        point2DSet = new TreeSet<>();
    }

    public boolean isEmpty() {
        return point2DSet.isEmpty();
    }

    public int size() {
        return point2DSet.size();
    }

    private void checkPointNotNull(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must not be null!");
        }
    }

    public void insert(Point2D p) {
        checkPointNotNull(p);
        point2DSet.add(p);
    }             // add the point to the set (if it is not already in the set)

    public boolean contains(Point2D p) {
        checkPointNotNull(p);
        return point2DSet.contains(p);
    }         // does the set contain point p?

    public void draw() {
        for (Point2D point2D : point2DSet) {
            point2D.draw();
        }
    }                       // draw all points to standard draw

    public Iterable<Point2D> range(RectHV rect) {
        Bag<Point2D> point2DList = new Bag<>();
        if (rect == null) {
            throw new IllegalArgumentException("Argument to range must not be null");
        }
        for (Point2D point2D : point2DSet) {
            if (rect.contains(point2D)) {
                point2DList.add(point2D);
            }
        }
        return point2DList;
    }

    public Point2D nearest(Point2D p) {
        checkPointNotNull(p);
        if (isEmpty()) {
            return null;
        } else {
            double curNearest = Double.POSITIVE_INFINITY;
            Point2D nearestPoint = null;
            for (Point2D curPoint : point2DSet) {
                final double curDistance = p.distanceTo(curPoint);
                if (curDistance < curNearest) {
                    curNearest = curDistance;
                    nearestPoint = curPoint;
                }
            }
            return nearestPoint;
        }
    }

    public static void main(String[] args) {

    }
}