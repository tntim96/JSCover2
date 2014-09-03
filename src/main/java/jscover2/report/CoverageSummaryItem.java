package jscover2.report;

public class CoverageSummaryItem {
    private int total;
    private int covered;

    public CoverageSummaryItem(int total, int covered) {
        this.total = total;
        this.covered = covered;
    }

    public float getRatio() {
        return (float) covered / total;
    }
}
