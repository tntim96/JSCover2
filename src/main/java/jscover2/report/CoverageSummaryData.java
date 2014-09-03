package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.HashSet;
import java.util.Set;

public class CoverageSummaryData {
    private final Integer zero = new Integer(0);
    private CoverageItem statements;
    private CoverageItem lines;
    private CoverageItem functions;
    private CoverageItem branches;
    private CoverageItem booleanExpressions;

    public CoverageSummaryData(ScriptObjectMirror mirror) {
        processStatements(mirror);
        processFunctions(mirror);
        processBooleanExpressions(mirror);
    }

    private void processStatements(ScriptObjectMirror mirror) {
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("s");
        ScriptObjectMirror map = (ScriptObjectMirror) mirror.get("sM");
        int totalStatements = data.size();
        int coveredStatements = 0;
        Set<Integer> linesTotal = new HashSet<>();
        Set<Integer> linesCovered = new HashSet<>();
        for (String count : data.keySet()) {
            int line = (int) ((ScriptObjectMirror) ((ScriptObjectMirror) map.get(count)).get("pos")).get("line");
            linesTotal.add(line);
            if (!zero.equals(data.get(count))) {
                coveredStatements++;
                linesCovered.add(line);
            }
        }
        statements = new CoverageItem(totalStatements, coveredStatements);
        lines = new CoverageItem(linesTotal.size(), linesCovered.size());
    }

    private void processBooleanExpressions(ScriptObjectMirror mirror) {
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("be");
        ScriptObjectMirror map = (ScriptObjectMirror) mirror.get("beM");
        int totalBEs = data.size() * 2;
        int coveredBEs = 0;
        int branchesTotal = 0;
        int branchesCovered = 0;
        for (String count : data.keySet()) {
            String branch = (String) ((ScriptObjectMirror) map.get(count)).get("br");
            if ("true".equals(branch))
                branchesTotal += 2;
            if (!zero.equals(((ScriptObjectMirror) data.get(count)).get("0"))) {
                coveredBEs++;
                if ("true".equals(branch))
                    branchesCovered++;
            }
            if (!zero.equals(((ScriptObjectMirror) data.get(count)).get("1"))) {
                coveredBEs++;
                if ("true".equals(branch))
                    branchesCovered++;
            }
        }
        booleanExpressions = new CoverageItem(totalBEs, coveredBEs);
        branches = new CoverageItem(branchesTotal, branchesCovered);
    }

    private void processFunctions(ScriptObjectMirror mirror) {
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("f");
        int total = data.size();
        int covered = 0;
        for (String count : data.keySet()) {
            if (!zero.equals(data.get(count)))
                covered++;
        }
        functions = new CoverageItem(total, covered);
    }

    public CoverageItem getStatements() {
        return statements;
    }

    public CoverageItem getLines() {
        return lines;
    }

    public CoverageItem getFunctions() {
        return functions;
    }

    public CoverageItem getBranches() {
        return branches;
    }

    public CoverageItem getBooleanExpressions() {
        return booleanExpressions;
    }
}
