package jscover2.report;

import java.util.ArrayList;
import java.util.List;

public class LineCompleteData {
    private List<CoverageData> statements = new ArrayList<>();

    public LineCompleteData() {
    }

    void addStatement(CoverageData data) {
        statements.add(data);
    }

    public List<CoverageData> getStatements() {
        return statements;
    }
}
