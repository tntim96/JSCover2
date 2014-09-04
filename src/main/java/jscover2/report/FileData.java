package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileData {
    private List<CoverageData> statements = new ArrayList<>();
    private List<LineData> lines = new ArrayList<>();
    private List<CoverageData> functions = new ArrayList<>();
    private List<BooleanExpressionData> booleanExpressions = new ArrayList<>();
    private List<BooleanExpressionData> branches = new ArrayList<>();

    public FileData(ScriptObjectMirror mirror) {
        processStatements(mirror);
        processFunctions(mirror);
        processBooleanExpressions(mirror);
    }

    private void processStatements(ScriptObjectMirror mirror) {
        Set<Integer> processedLines = new HashSet<>();
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("s");
        ScriptObjectMirror map = (ScriptObjectMirror) mirror.get("sM");
        for (String count : data.keySet()) {
            int hits = (int) data.get(count);
            ScriptObjectMirror pos = (ScriptObjectMirror) ((ScriptObjectMirror) map.get(count)).get("pos");
            PositionData positionData = new PositionData(pos);
            statements.add(new CoverageData(hits, positionData));
            if (processedLines.add(positionData.getLine()))
                lines.add(new LineData(hits, positionData.getLine()));
        }
    }

    private void processFunctions(ScriptObjectMirror mirror) {
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("f");
        ScriptObjectMirror map = (ScriptObjectMirror) mirror.get("fM");
        for (String count : data.keySet()) {
            int hits = (int) data.get(count);
            ScriptObjectMirror pos = (ScriptObjectMirror) ((ScriptObjectMirror) map.get(count)).get("pos");
            PositionData positionData = new PositionData(pos);
            functions.add(new CoverageData(hits, positionData));
        }
    }

    private void processBooleanExpressions(ScriptObjectMirror mirror) {
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("be");
        ScriptObjectMirror map = (ScriptObjectMirror) mirror.get("beM");
        for (String count : data.keySet()) {
            int trueHits = (int) ((ScriptObjectMirror) data.get(count)).get(0);
            int falseHits = (int) ((ScriptObjectMirror) data.get(count)).get(1);
            ScriptObjectMirror pos = (ScriptObjectMirror) ((ScriptObjectMirror) map.get(count)).get("pos");
            PositionData positionData = new PositionData(pos);
            boolean branch = ((ScriptObjectMirror) map.get(count)).get("br").equals("true");
            BooleanExpressionData be = new BooleanExpressionData(falseHits, trueHits, positionData, branch);
            booleanExpressions.add(be);
            if (branch)
                branches.add(be);
        }
    }

    public List<CoverageData> getStatements() {
        return statements;
    }

    public List<LineData> getLines() {
        return lines;
    }

    public List<CoverageData> getFunctions() {
        return functions;
    }

    public List<BooleanExpressionData> getBooleanExpressions() {
        return booleanExpressions;
    }

    public List<BooleanExpressionData> getBranches() {
        return branches;
    }
}
