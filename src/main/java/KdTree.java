import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;

    private static class Node {
        private final Point2D key;
        private Node left, right;
        private int size;
        private final RectHV rect;

        public Node(Point2D key, int size, RectHV rect) {
            this.key = key;
            this.size = size;
            this.rect = rect;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key.toString() +
                    ", rect=" + rect.toString() +
                    '}';
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

        root = insert(root, p, true, 0, 0, 1, 1);
    }

    private Node insert(Node node, Point2D p, final boolean compareByX, double xMin, double yMin, double xMax, double yMax) {
        if (node == null) return new Node(p, 1, new RectHV(xMin, yMin, xMax, yMax));

        if (!node.key.equals(p)) {
            double cmp = compareByX
                    ? p.x() - node.key.x()
                    : p.y() - node.key.y();

            if (cmp < 0) {
                if (compareByX) {
                    xMax = node.key.x();
                } else {
                    yMax = node.key.y();
                }
                node.left = insert(node.left, p, !compareByX, xMin, yMin, xMax, yMax);
            } else {
                if (compareByX) {
                    xMin = node.key.x();
                } else {
                    yMin = node.key.y();
                }
                node.right = insert(node.right, p, !compareByX, xMin, yMin, xMax, yMax);
            }
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
            if (p.equals(node.key)) {
                return true;
            }
            double cmp = compareByX
                    ? p.x() - node.key.x()
                    : p.y() - node.key.y();
            if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
            compareByX = !compareByX;
        }
        return false;
    }

    private void drawNode(Node node, boolean compareByX, Node prevNode, double xStart, double yStart, double xEnd, double yEnd) {
        if (node == null) return;
        final Point2D p = node.key;
        System.out.println("Node: " + node);
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
            if (prevPoint != null && prevPoint.y() <= p.y()) {
                yStart = prevPoint.y();
            } else if (prevPoint != null) {
                yEnd = prevPoint.y();
            }
            startPoint = new Point2D(p.x(), yStart);
            endPoint = new Point2D(p.x(), yEnd);
            StdDraw.setPenColor(StdDraw.RED);
        } else {
            if (prevPoint != null && prevPoint.x() <= p.x()) {
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
        Bag<Point2D> pointBag = new Bag<>();
        range(rect, root, pointBag);
        return pointBag;
    }

    private void range(RectHV queryRect, Node node, Bag<Point2D> pointBag) {
        if (node == null) return;

        if (node.rect.intersects(queryRect)) {
            if (queryRect.contains(node.key)) {
                pointBag.add(node.key);
            }
            range(queryRect, node.left, pointBag);
            range(queryRect, node.right, pointBag);
        }
    }

    public Point2D nearest(Point2D p) {
        if (isEmpty()) {
            return null;
        }
        Node nearestNode = nearest(p, root, null, true);
        return nearestNode.key;
    }

    private Node nearest(Point2D queryPoint, Node node, Node curNearestNode, boolean compareByX) {
        if (node == null) return curNearestNode;

        double curDistance = node.key.distanceSquaredTo(queryPoint);
        double nearestDistance = curNearestNode == null ? Double.POSITIVE_INFINITY : curNearestNode.key.distanceSquaredTo(queryPoint);
        double distanceToNodeRectangle = node.rect.distanceSquaredTo(queryPoint);
        if (nearestDistance <= distanceToNodeRectangle) {
            return curNearestNode;
        }
        if (curDistance < nearestDistance) {
            curNearestNode = node;
        }

        double cmp = compareByX
                ? queryPoint.x() - node.key.x()
                : queryPoint.y() - node.key.y();
        if (cmp < 0) {
            curNearestNode = nearest(queryPoint, node.left, curNearestNode, !compareByX);
            double distanceToRightNodeRectangle = node.right == null ? Double.POSITIVE_INFINITY : node.right.rect.distanceSquaredTo(queryPoint);
            if (curNearestNode.key.distanceSquaredTo(queryPoint) > distanceToRightNodeRectangle) {
                curNearestNode = nearest(queryPoint, node.right, curNearestNode, !compareByX);
            }
        } else {
            curNearestNode = nearest(queryPoint, node.right, curNearestNode, !compareByX);
            double distanceToLeftNodeRectangle = node.left == null ? Double.POSITIVE_INFINITY : node.left.rect.distanceSquaredTo(queryPoint);
            if (curNearestNode.key.distanceSquaredTo(queryPoint) > distanceToLeftNodeRectangle) {
                curNearestNode = nearest(queryPoint, node.left, curNearestNode, !compareByX);
            }
        }
        return curNearestNode;
    }

}
