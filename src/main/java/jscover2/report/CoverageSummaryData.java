package jscover2.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoverageSummaryData {
    private CoverageSummaryItem statementCoverage;
    private CoverageSummaryItem lineCoverage;
    private CoverageSummaryItem functionCoverage;
    private CoverageSummaryItem booleanExpressionCoverage;

    public CoverageSummaryData(FileData fileData) {
        processStatements(fileData.getStatements());
        processFunctions(fileData.getFunctions());
        processBooleanExpressions(fileData.getBooleanExpressions());
    }

    private void processStatements(List<CoverageData> statements) {
        Map<Integer, Boolean> linesHit = new HashMap<>();
        int covered = 0;
        for (CoverageData coverageData : statements) {
            int line = coverageData.getPosition().getLine();
            if (!linesHit.keySet().contains(line))
                linesHit.put(line, false);
            if (coverageData.getHits() > 0) {
                covered++;
                linesHit.put(line, true);
            }
        }
        int linesCovered = 0;
        for (boolean hit : linesHit.values())
            if (hit)
                linesCovered++;

        statementCoverage = new CoverageSummaryItem(statements.size(), covered);
        lineCoverage = new CoverageSummaryItem(linesHit.size(), linesCovered);
    }

    private void processFunctions(List<CoverageData> functions) {
        int covered = 0;
        for (CoverageData coverageData : functions)
            if (coverageData.getHits() > 0)
                covered++;
        functionCoverage = new CoverageSummaryItem(functions.size(), covered);
    }

    private void processBooleanExpressions(List<BooleanExpressionData> booleanExpressions) {
        int covered = 0;
        for (BooleanExpressionData coverageData : booleanExpressions) {
            if (coverageData.getFalseHits() > 0 && coverageData.getTrueHits() > 0)
                covered++;
        }

        booleanExpressionCoverage = new CoverageSummaryItem(booleanExpressions.size(), covered);
    }

    public CoverageSummaryItem getStatementCoverage() {
        return statementCoverage;
    }

    public CoverageSummaryItem getLineCoverage() {
        return lineCoverage;
    }

    public CoverageSummaryItem getFunctionCoverage() {
        return functionCoverage;
    }

    public CoverageSummaryItem getBooleanExpressionCoverage() {
        return booleanExpressionCoverage;
    }
}
