package jscover2.report;

public class CoverageItem {
    private int total;
    private int covered;

    public CoverageItem(int total, int covered) {
        this.total = total;
        this.covered = covered;
    }

    public float getRatio() {
        return (float) covered / total;
    }
}
