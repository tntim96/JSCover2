package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.HashSet;
import java.util.Set;

import static jscover2.report.Constants.zero;

public class CoverageSummaryData {
    private CoverageSummaryItem statements;
    private CoverageSummaryItem lines;
    private CoverageSummaryItem functions;
    private CoverageSummaryItem branches;
    private CoverageSummaryItem booleanExpressions;

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
        statements = new CoverageSummaryItem(totalStatements, coveredStatements);
        lines = new CoverageSummaryItem(linesTotal.size(), linesCovered.size());
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
        booleanExpressions = new CoverageSummaryItem(totalBEs, coveredBEs);
        branches = new CoverageSummaryItem(branchesTotal, branchesCovered);
    }

    private void processFunctions(ScriptObjectMirror mirror) {
        ScriptObjectMirror data = (ScriptObjectMirror) mirror.get("f");
        int total = data.size();
        int covered = 0;
        for (String count : data.keySet()) {
            if (!zero.equals(data.get(count)))
                covered++;
        }
        functions = new CoverageSummaryItem(total, covered);
    }

    public CoverageSummaryItem getStatements() {
        return statements;
    }

    public CoverageSummaryItem getLines() {
        return lines;
    }

    public CoverageSummaryItem getFunctions() {
        return functions;
    }

    public CoverageSummaryItem getBranches() {
        return branches;
    }

    public CoverageSummaryItem getBooleanExpressions() {
        return booleanExpressions;
    }
}
