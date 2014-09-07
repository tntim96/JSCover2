package jscover2.report;

public class CoverageSummaryItem {
    private int total;
    private int covered;
    private float ratio;

    public CoverageSummaryItem(int total, int covered) {
        this.total = total;
        this.covered = covered;
        this.ratio = (float) covered / total;
    }

    public void merge(CoverageSummaryItem item) {
        this.total += item.total;
        this.covered += item.covered;
        this.ratio = (float) covered / total;
    }

    public float getRatio() {
        return ratio;
    }
}
