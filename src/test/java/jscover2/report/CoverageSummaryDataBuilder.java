package jscover2.report;

import jscover2.utils.ReflectionUtils;

public class CoverageSummaryDataBuilder {
    private String name;
    private CoverageSummaryItem statementCoverage;
    private CoverageSummaryItem lineCoverage;
    private CoverageSummaryItem functionCoverage;
    private CoverageSummaryItem booleanExpressionCoverage;
    private CoverageSummaryItem branchPathCoverage;

    public CoverageSummaryDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CoverageSummaryDataBuilder withStatementCoverage(int hits, int total) {
        statementCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withLineCoverage(int hits, int total) {
        lineCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withFunctionCoverage(int hits, int total) {
        functionCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withBooleanExpressionCoverage(int hits, int total) {
        booleanExpressionCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryDataBuilder withBranchPathCoverage(int hits, int total) {
        branchPathCoverage = new CoverageSummaryItem(hits, total);
        return this;
    }

    public CoverageSummaryData build() {
        CoverageSummaryData data = new CoverageSummaryData();
        ReflectionUtils.setField(data, "name", name);
        ReflectionUtils.setField(data, "statementCoverage", statementCoverage);
        ReflectionUtils.setField(data, "lineCoverage", lineCoverage);
        ReflectionUtils.setField(data, "functionCoverage", functionCoverage);
        ReflectionUtils.setField(data, "booleanExpressionCoverage", booleanExpressionCoverage);
        ReflectionUtils.setField(data, "branchPathCoverage", branchPathCoverage);
        return data;
    }
}
