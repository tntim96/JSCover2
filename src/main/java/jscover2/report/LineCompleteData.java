package jscover2.report;

import java.util.ArrayList;
import java.util.List;

public class LineCompleteData {
    private List<CoverageData> statements = new ArrayList<>();

    public LineCompleteData() {
    }

    public boolean hit() {
        for (CoverageData statement : statements)
            if (statement.getHits() > 0)
                return true;
        return false;
    }

    void addStatement(CoverageData data) {
        statements.add(data);
    }

    public List<CoverageData> getStatements() {
        return statements;
    }
}
