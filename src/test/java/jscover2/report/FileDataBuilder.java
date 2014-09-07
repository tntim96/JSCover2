package jscover2.report;

import jscover2.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FileDataBuilder {
    private List<CoverageData> statements = new ArrayList<>();
    private List<CoverageData> functions = new ArrayList<>();
    private List<BooleanExpressionData> booleanExpressions = new ArrayList<>();
    private List<BooleanExpressionData> booleanBranches = new ArrayList<>();
    private List<CoverageData> branchPaths = new ArrayList<>();

    public FileDataBuilder withStatementCoverage(CoverageData data) {
        this.statements.add(data);
        return this;
    }

    public FileDataBuilder withFunctionCoverage(CoverageData data) {
        this.functions.add(data);
        return this;
    }

    public FileDataBuilder withBooleanExpressionsCoverage(BooleanExpressionData data) {
        this.booleanExpressions.add(data);
        return this;
    }

    public FileDataBuilder withBooleanBranch(BooleanExpressionData data) {
        this.booleanBranches.add(data);
        return this;
    }

    public FileDataBuilder withBranchPath(CoverageData data) {
        this.branchPaths.add(data);
        return this;
    }

    public FileData build() {
        FileData fileData = ReflectionUtils.newInstance(FileData.class);
        ReflectionUtils.setField(fileData, "statements", statements);
        ReflectionUtils.setField(fileData, "functions", functions);
        ReflectionUtils.setField(fileData, "booleanExpressions", booleanExpressions);
        ReflectionUtils.setField(fileData, "booleanBranches", booleanBranches);
        ReflectionUtils.setField(fileData, "branchPaths", branchPaths);
        return fileData;
    }
}
