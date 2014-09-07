package jscover2.report;

import jscover2.utils.ReflectionUtils;

public class CoverageSummaryDataBuilder {
    private CoverageSummaryItem statementCoverage;
    private CoverageSummaryItem functionCoverage;
    private CoverageSummaryItem booleanExpressionCoverage;
    private CoverageSummaryItem branchPathCoverage;

    public CoverageSummaryDataBuilder withStatementCoverage(int hits, int total) {
        statementCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withFunctionCoverage(int hits, int total) {
        functionCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withBooleanExpressionsCoverage(int hits, int total) {
        booleanExpressionCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withBranchPath(int hits, int total) {
        branchPathCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryData build() {
        CoverageSummaryData data = new CoverageSummaryData();
        ReflectionUtils.setField(data, "statementCoverage", statementCoverage);
        ReflectionUtils.setField(data, "functionCoverage", functionCoverage);
        ReflectionUtils.setField(data, "booleanExpressionCoverage", booleanExpressionCoverage);
        ReflectionUtils.setField(data, "branchPathCoverage", branchPathCoverage);
        return data;
    }
}
