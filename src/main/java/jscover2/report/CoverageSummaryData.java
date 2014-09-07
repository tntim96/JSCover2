package jscover2.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoverageSummaryData {
    private String name;
    private CoverageSummaryItem statementCoverage;
    private CoverageSummaryItem lineCoverage;
    private CoverageSummaryItem functionCoverage;
    private CoverageSummaryItem booleanExpressionCoverage;
    private CoverageSummaryItem branchPathCoverage;

    public CoverageSummaryData() {
        name = "Total";
        statementCoverage = new CoverageSummaryItem(0, 0);
        lineCoverage = new CoverageSummaryItem(0, 0);
        functionCoverage = new CoverageSummaryItem(0, 0);
        booleanExpressionCoverage = new CoverageSummaryItem(0, 0);
        branchPathCoverage = new CoverageSummaryItem(0, 0);
    }

    public CoverageSummaryData(String name, FileData fileData) {
        this.name = name;
        processStatements(fileData.getStatements());
        processFunctions(fileData.getFunctions());
        processBooleanExpressions(fileData.getBooleanExpressions());
        calculateBranchPathCoverage(fileData.getBooleanBranches(), fileData.getBranchPaths());
    }

    public void add(CoverageSummaryData data) {
        statementCoverage.add(data.getStatementCoverage());
        lineCoverage.add(data.getLineCoverage());
        functionCoverage.add(data.getFunctionCoverage());
        booleanExpressionCoverage.add(data.getBooleanExpressionCoverage());
        branchPathCoverage.add(data.getBranchPathCoverage());
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

        statementCoverage = new CoverageSummaryItem(covered, statements.size());
        lineCoverage = new CoverageSummaryItem(linesCovered, linesHit.size());
    }

    private void processFunctions(List<CoverageData> functions) {
        int covered = 0;
        for (CoverageData coverageData : functions)
            if (coverageData.getHits() > 0)
                covered++;
        functionCoverage = new CoverageSummaryItem(covered, functions.size());
    }

    private void calculateBranchPathCoverage(List<BooleanExpressionData> booleanBranches, List<CoverageData> branchPaths) {
        int covered = 0;
        for (BooleanExpressionData coverageData : booleanBranches) {
            if (coverageData.getFalseHits() > 0)
                covered++;
            if (coverageData.getTrueHits() > 0)
                covered++;
        }
        for (CoverageData coverageData : branchPaths)
            if (coverageData.getHits() > 0)
                covered++;
        int total = booleanBranches.size() * 2 + branchPaths.size();
        branchPathCoverage = new CoverageSummaryItem(covered, total);
    }

    private void processBooleanExpressions(List<BooleanExpressionData> booleanExpressions) {
        int covered = 0;
        for (BooleanExpressionData coverageData : booleanExpressions)
            if (coverageData.getFalseHits() > 0 && coverageData.getTrueHits() > 0)
                covered++;
        booleanExpressionCoverage = new CoverageSummaryItem(covered, booleanExpressions.size());
    }

    public String getName() {
        return name;
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

    public CoverageSummaryItem getBranchPathCoverage() {
        return branchPathCoverage;
    }
}
