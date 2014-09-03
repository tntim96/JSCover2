package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.HashSet;
import java.util.Set;

public class CoverageData {
    private final Integer zero = new Integer(0);
    private CoverageItem statements;
    private CoverageItem lines;

    public CoverageData(ScriptObjectMirror mirror) {
        processStatements(mirror);
    }

    private void processStatements(ScriptObjectMirror mirror) {
        ScriptObjectMirror statementObject = (ScriptObjectMirror) mirror.get("s");
        ScriptObjectMirror statementMap = (ScriptObjectMirror) mirror.get("sM");
        int totalStatements = statementObject.size();
        int coveredStatements = 0;
        Set<Integer> linesTotal = new HashSet<>();
        Set<Integer> linesCovered = new HashSet<>();
        for (String stmt : statementObject.keySet()) {
            int line = (int)((ScriptObjectMirror)((ScriptObjectMirror)statementMap.get(stmt)).get("pos")).get("line");
            linesTotal.add(line);
            if (!zero.equals(statementObject.get(stmt))) {
                coveredStatements++;
                linesCovered.add(line);
            }
        }
        statements = new CoverageItem(totalStatements, coveredStatements);
        lines = new CoverageItem(linesTotal.size(), linesCovered.size());
    }

    public CoverageItem getStatements() {
        return statements;
    }

    public CoverageItem getLines() {
        return lines;
    }
}
