package jscover2.report;

public class PositionData {
    private int line;
    private int column;
    private int length;

    protected PositionData(int line, int column, int length) {
        this.line = line;
        this.column = column;
        this.length = length;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }
}
