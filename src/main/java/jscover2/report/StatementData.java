package jscover2.report;

public class StatementData {
    private int hits;
    private PositionData position;

    public StatementData(int hits, PositionData position) {
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
