import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;
    private static final double DEFAULT_DOUBLE = 0.0d;

    private final int n;
    private final int trials;
    private final double[] percolationThresholdArray;
    private final double meanPercolationThreshold;
    private final double stdDevPercolationThreshold;
    private final double confidenceLo;
    private final double confidenceHi;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) {
            throw new IllegalArgumentException(String.format("n must be positive, but got %d", n));
        }
        this.n = n;
        if (trials <= 1) {
            throw new IllegalArgumentException(String.format("trials must be more than 1, but got %d", trials));
        }
        this.trials = trials;
        percolationThresholdArray = new double[trials];
        for (int i = 0; i < trials; i++) {
            double percolationThreshold = calculatePercolationThreshold();
            percolationThresholdArray[i] = percolationThreshold;
        }
        meanPercolationThreshold = mean();
        stdDevPercolationThreshold = stddev();
        confidenceLo = confidenceLo();
        confidenceHi = confidenceHi();
    }

    private int siteIdxToRow(int siteIdx) {
        return (siteIdx / n) + 1;
    }

    private int siteIdxToCol(int siteIdx) {
        return (siteIdx % n) + 1;
    }

    private double calculatePercolationThreshold() {
        Percolation percolation = new Percolation(n);
        while (!percolation.percolates()) {
            final int blockedSiteIdx = StdRandom.uniform(n * n);
            final int blockedSiteRow = siteIdxToRow(blockedSiteIdx);
            final int blockedSiteCol = siteIdxToCol(blockedSiteIdx);
            percolation.open(blockedSiteRow, blockedSiteCol);
        }
        return ((double) percolation.numberOfOpenSites()) / (n * n);
    }

    // sample mean of percolation threshold
    public double mean() {
        if (meanPercolationThreshold != DEFAULT_DOUBLE) {
            return meanPercolationThreshold;
        } else {
            return StdStats.mean(percolationThresholdArray);
        }
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        if (stdDevPercolationThreshold != DEFAULT_DOUBLE) {
            return stdDevPercolationThreshold;
        } else {
            return StdStats.stddev(percolationThresholdArray);
        }
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        if (confidenceLo != DEFAULT_DOUBLE) {
            return confidenceLo;
        } else {
            return meanPercolationThreshold - (CONFIDENCE_95 * stdDevPercolationThreshold / Math.sqrt(trials));
        }
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        if (confidenceHi != DEFAULT_DOUBLE) {
            return confidenceHi;
        } else {
            return meanPercolationThreshold + (CONFIDENCE_95 * stdDevPercolationThreshold / Math.sqrt(trials));
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, trials);
        System.out.printf("mean                    = %.16f%n", percolationStats.mean());
        System.out.printf("stddev                  = %.16f%n", percolationStats.stddev());
        System.out.printf("95%% confidence interval = [%.16f, %.16f]", percolationStats.confidenceLo(), percolationStats.confidenceHi());
    }

}
