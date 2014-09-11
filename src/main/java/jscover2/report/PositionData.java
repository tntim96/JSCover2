package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class PositionData {
    private int line;
    private int column;
    private int length;

    private PositionData() {}

    protected PositionData(ScriptObjectMirror pos) {
        this.line = (int) pos.get("line");
        this.column = (int) pos.get("col");
        this.length = (int) pos.get("len");
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
