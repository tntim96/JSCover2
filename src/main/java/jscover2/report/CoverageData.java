package jscover2.report;

public class CoverageData {
    private int hits;
    private PositionData position;

    public CoverageData(int hits, PositionData position) {
        this.hits = hits;
        this.position = position;
    }

    public int getHits() {
        return hits;
    }

    public PositionData getPosition() {
        return position;
    }
}
