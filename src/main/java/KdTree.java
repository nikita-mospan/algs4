import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;

    private static class Node {
        private final Point2D key;
        private Node left, right;
        private int size;

        public Node(Point2D key, int size) {
            this.key = key;
            this.size = size;
        }
    }

    public KdTree() {
    }

    public boolean isEmpty() {
        return root == null;
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) return 0;
        return x.size;
    }

    public int size() {
        if (root == null) return 0;
        return root.size;
    }

    private void checkPointNotNull(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must not be null!");
        }
    }

    public void insert(Point2D p) {
        checkPointNotNull(p);

        root = insert(root, p, true);
    }

    private Node insert(Node node, Point2D p, final boolean compareByX) {
        if (node == null) return new Node(p, 1);

        double cmp = compareByX
                ? p.x() - node.key.x()
                : p.y() - node.key.y();
        if (cmp < 0) {
            node.left = insert(node.left, p, !compareByX);
        }
        else if (cmp > 0) {
            node.right = insert(node.right, p, !compareByX);
        }

        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    public boolean contains(Point2D p) {
        checkPointNotNull(p);
        return get(root, p);
    }

    private boolean get(Node node, Point2D p) {
        boolean compareByX = true;
        while (node != null) {
            double cmp = compareByX
                    ? p.x() - node.key.x()
                    : p.y() - node.key.y();
            if (cmp < 0) {
                node = node.left;
            }
            else if (cmp > 0) {
                node = node.right;
            }
            else {
                return true;
            }
            compareByX = !compareByX;
        }
        return false;
    }

    private void drawNode(Node node, boolean compareByX, Node prevNode, double xStart, double yStart, double xEnd, double yEnd) {
        if (node == null) return;
        final Point2D p = node.key;
        System.out.println("Point: " + p);
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor();
        p.draw();
        StdDraw.setPenRadius();
        Point2D prevPoint = null;
        if (prevNode != null) {
            prevPoint = prevNode.key;
        }
        Point2D startPoint, endPoint;
        if (compareByX) {
            if (prevPoint != null && prevPoint.y() < p.y()) {
                yStart = prevPoint.y();
            } else if (prevPoint != null) {
                yEnd = prevPoint.y();
            }
            startPoint = new Point2D(p.x(), yStart);
            endPoint = new Point2D(p.x(), yEnd);
            StdDraw.setPenColor(StdDraw.RED);
        } else {
            if (prevPoint != null && prevPoint.x() < p.x()) {
                xStart = prevPoint.x();
            } else if (prevPoint != null) {
                xEnd = prevPoint.x();
            }
            startPoint = new Point2D(xStart, p.y());
            endPoint = new Point2D(xEnd, p.y());
            StdDraw.setPenColor(StdDraw.BLUE);
        }
        System.out.println("startPoint: " + startPoint);
        System.out.println("endPoint: " + endPoint);
        startPoint.drawTo(endPoint);
        drawNode(node.left, !compareByX, node, xStart, yStart, xEnd, yEnd);
        drawNode(node.right, !compareByX, node, xStart, yStart, xEnd, yEnd);
    }

    public void draw() {
        drawNode(root, true, null, 0, 0, 1, 1);
    }

    public Iterable<Point2D> range(RectHV rect) {
        return null;
    }

    public Point2D nearest(Point2D p) {
        return null;
    }
}
