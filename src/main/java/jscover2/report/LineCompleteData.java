package jscover2.report;

import java.util.ArrayList;
import java.util.List;

public class LineCompleteData {
    private List<CoverageData> statements = new ArrayList<>();
    private List<CoverageData> branchPaths = new ArrayList<>();
    private int lineHits = 0;

    public LineCompleteData() {
    }

    void addStatement(CoverageData data) {
        statements.add(data);
        if (lineHits == 0)
            lineHits = data.getHits();
    }

    void addBranch(CoverageData data) {
        branchPaths.add(data);
        if (lineHits == 0)
            lineHits = data.getHits();
    }

    public boolean hit() {
        return lineHits > 0;
    }

    public int getLineHits() {
        return lineHits;
    }

    public List<CoverageData> getStatements() {
        return statements;
    }

    public List<CoverageData> getBranchPaths() {
        return branchPaths;
    }
}
