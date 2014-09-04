package jscover2.report;

public class LineData {
    private int hits;
    private int line;

    public LineData(int hits, int line) {
        this.hits = hits;
        this.line = line;
    }

    public int getHits() {
        return hits;
    }

    public int getLine() {
        return line;
    }
}
