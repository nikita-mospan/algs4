import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int n;
    private final int virtualTopSite;
    private final int virtualBottomSite;
    // true - site is open
    // false - site is blocked
    private final boolean[] sites;
    private final WeightedQuickUnionUF weightedQuickUnionUF;
    private final WeightedQuickUnionUF noVirtualBottomSiteQuickUnionUF;
    private int numberOfOpenSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(String.format("n must be positive, but got %d", n));
        }
        this.n = n;
        // plus 2 sites to create two virtual sites (one that connects to sites from top row and one that connects to sites from bottom row)
        int size = n * n + 2;
        sites = new boolean[size];
        for (int i = 0; i < size; i++) {
            sites[i] = false;
        }
        numberOfOpenSites = 0;
        virtualTopSite = size - 2;
        virtualBottomSite = size - 1;
        sites[virtualTopSite] = true;
        sites[virtualBottomSite] = true;
        weightedQuickUnionUF = new WeightedQuickUnionUF(size);
        noVirtualBottomSiteQuickUnionUF = new WeightedQuickUnionUF(size - 1);
    }

    private void validateRowCol(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException(
                    String.format("Value of row and col must be between 1 and %d, but got row: %d, col: %d", n, row, col));
        }
    }

    private int getSiteIdx(int row, int col) {
        int rowIdx = row - 1;
        int colIdx = col - 1;
        return rowIdx * n + colIdx;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validateRowCol(row, col);
        if (isOpen(row, col)) {
            return;
        }
        final int siteIdx = getSiteIdx(row, col);
        sites[siteIdx] = true;
        numberOfOpenSites++;
        unionOpenNeighbors(row, col, row, col - 1);
        unionOpenNeighbors(row, col, row - 1, col);
        unionOpenNeighbors(row, col, row, col + 1);
        unionOpenNeighbors(row, col, row + 1, col);
    }

    private void unionOpenNeighbors(int row, int col, int neighborRow, int neighborCol) {
        int siteIdx = getSiteIdx(row, col);
        if (neighborRow == 0) {
            weightedQuickUnionUF.union(siteIdx, virtualTopSite);
            noVirtualBottomSiteQuickUnionUF.union(siteIdx, virtualTopSite);
        } else if (neighborRow == n + 1) {
            weightedQuickUnionUF.union(siteIdx, virtualBottomSite);
        } else if (neighborRow >= 1 && neighborRow <= n && neighborCol >= 1 && neighborCol <= n && isOpen(neighborRow, neighborCol)) {
            weightedQuickUnionUF.union(siteIdx, getSiteIdx(neighborRow, neighborCol));
            noVirtualBottomSiteQuickUnionUF.union(siteIdx, getSiteIdx(neighborRow, neighborCol));
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateRowCol(row, col);
        return sites[getSiteIdx(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validateRowCol(row, col);
        return noVirtualBottomSiteQuickUnionUF.find(getSiteIdx(row, col)) == noVirtualBottomSiteQuickUnionUF.find(virtualTopSite);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return weightedQuickUnionUF.find(virtualTopSite) == weightedQuickUnionUF.find(virtualBottomSite);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= n; col++) {
                stringBuilder.append(sites[getSiteIdx(row, col)] ? "1" : "0").append(" ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    // test client (optional)
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
