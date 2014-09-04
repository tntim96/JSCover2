package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.*;

public class FileData {
    private List<StatementData> statements = new ArrayList<>();
    private List<LineData> lines = new ArrayList<>();

    public FileData(ScriptObjectMirror mirror) {
        processStatements(mirror);
    }

    private void processStatements(ScriptObjectMirror mirror) {
        Set<Integer> processedLines = new HashSet<>();
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("s");
        ScriptObjectMirror map = (ScriptObjectMirror) mirror.get("sM");
        for (String count : data.keySet()) {
            int hits = (int) data.get(count);
            ScriptObjectMirror pos = (ScriptObjectMirror) ((ScriptObjectMirror) map.get(count)).get("pos");
            int line = (int) pos.get("line");
            int column = (int) pos.get("col");
            int length = (int) pos.get("len");
            PositionData positionData = new PositionData(line, column, length);
            statements.add(new StatementData(hits, positionData));
            if (processedLines.add(line))
                lines.add(new LineData(hits, line));
        }
    }

    public List<StatementData> getStatements() {
        return statements;
    }

    public List<LineData> getLines() {
        return lines;
    }
}
