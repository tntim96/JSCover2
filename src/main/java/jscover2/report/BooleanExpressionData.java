package jscover2.report;

public class BooleanExpressionData {
    private int falseHits;
    private int trueHits;
    private PositionData position;
    private boolean branch;

    public BooleanExpressionData(int falseHits, int trueHits, PositionData position, boolean branch) {
        this.falseHits = falseHits;
        this.trueHits = trueHits;
        this.position = position;
        this.branch = branch;
    }

    public boolean hit() {
        return falseHits > 0 && trueHits > 0;
    }

    public int getFalseHits() {
        return falseHits;
    }

    public int getTrueHits() {
        return trueHits;
    }

    public PositionData getPosition() {
        return position;
    }

    public boolean isBranch() {
        return branch;
    }
}
