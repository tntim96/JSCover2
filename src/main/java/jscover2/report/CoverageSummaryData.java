package jscover2.report;

import java.util.HashMap;
import java.util.Map;

public class CoverageSummaryData {
    private CoverageSummaryItem statements;
    private CoverageSummaryItem lines;
    private CoverageSummaryItem functions;

    public CoverageSummaryData(FileData fileData) {
        processStatements(fileData);
        processFunctions(fileData);
    }

    private void processStatements(FileData fileData) {
        Map<Integer, Boolean> linesHit = new HashMap<>();
        int covered = 0;
        for (CoverageData coverageData : fileData.getStatements()) {
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

        statements = new CoverageSummaryItem(fileData.getStatements().size(), covered);
        lines = new CoverageSummaryItem(linesHit.size(), linesCovered);
    }

    private void processFunctions(FileData fileData) {
        int covered = 0;
        for (CoverageData coverageData : fileData.getFunctions())
            if (coverageData.getHits() > 0)
                covered++;
        functions = new CoverageSummaryItem(fileData.getFunctions().size(), covered);
    }

    public CoverageSummaryItem getStatements() {
        return statements;
    }

    public CoverageSummaryItem getLines() {
        return lines;
    }

    public CoverageSummaryItem getFunctions() {
        return functions;
    }
}
