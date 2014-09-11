package jscover2.report;

import java.util.ArrayList;
import java.util.List;

public class LineCompleteData {
    private List<CoverageData> statements = new ArrayList<>();
    private int hits = 0;

    public LineCompleteData() {
    }

    void addStatement(CoverageData data) {
        statements.add(data);
        if (hits == 0)
            hits = data.getHits();
    }

    public int getLineHits() {
        return hits;
    }

    public List<CoverageData> getStatements() {
        return statements;
    }
}
