package jscover2.report;

public class CoverageSummaryItem {
    private int total;
    private int covered;
    private float ratio;

    public CoverageSummaryItem(int covered, int total) {
        this.covered = covered;
        this.total = total;
        this.ratio = (float) covered / total;
    }

    public void add(CoverageSummaryItem item) {
        this.covered += item.covered;
        this.total += item.total;
        this.ratio = (float) covered / total;
    }

    public int getCovered() {
        return covered;
    }

    public int getTotal() {
        return total;
    }

    public float getRatio() {
        return ratio;
    }
}
