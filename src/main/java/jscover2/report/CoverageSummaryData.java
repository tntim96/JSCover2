package jscover2.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoverageSummaryData {
    private CoverageSummaryItem statements;
    private CoverageSummaryItem lines;

    public CoverageSummaryData(FileData fileData) {
        processStatements(fileData);
    }

    private void processStatements(FileData fileData) {
        procesStatements(fileData);
    }

    private void procesStatements(FileData fileData) {
        Map<Integer, Boolean> linesHit = new HashMap<>();
        int statementsCovered = 0;
        for (CoverageData coverageData : fileData.getStatements()) {
            int line = coverageData.getPosition().getLine();
            if (!linesHit.keySet().contains(line))
                linesHit.put(line, false);
            if (coverageData.getHits() > 0) {
                statementsCovered++;
                linesHit.put(line, true);
            }
        }
        int linesCovered = 0;
        for (boolean hit : linesHit.values())
            if (hit)
                linesCovered++;

        statements = new CoverageSummaryItem(fileData.getStatements().size(), statementsCovered);
        lines = new CoverageSummaryItem(linesHit.size(), linesCovered);
    }

    public CoverageSummaryItem getStatements() {
        return statements;
    }

    public CoverageSummaryItem getLines() {
        return lines;
    }
}
