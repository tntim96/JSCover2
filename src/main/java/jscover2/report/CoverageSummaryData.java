package jscover2.report;

public class CoverageSummaryData {
    private CoverageSummaryItem statements;

    public CoverageSummaryData(FileData fileData) {
        processStatements(fileData);
    }

    private void processStatements(FileData fileData) {
        procesStatements(fileData);
    }

    private void procesStatements(FileData fileData) {
        int covered = 0;
        for (CoverageData coverageData : fileData.getStatements()) {
            if (coverageData.getHits() > 0)
                covered++;
        }
        statements = new CoverageSummaryItem(fileData.getStatements().size(), covered);
    }

    public CoverageSummaryItem getStatements() {
        return statements;
    }
}
