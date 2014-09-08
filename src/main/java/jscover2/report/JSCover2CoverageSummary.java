package jscover2.report;

import java.util.*;

public class JSCover2CoverageSummary {
    private List<CoverageSummaryData> files = new ArrayList<>();
    private CoverageSummaryData totals = new CoverageSummaryData();
    private CoverageSummaryDataSorter sorter = new CoverageSummaryDataSorter();

    private JSCover2CoverageSummary() {}

    public JSCover2CoverageSummary(JSCover2Data data) {
        for (String uriPath : data.getDataMap().keySet()) {
            FileData fileData = data.getDataMap().get(uriPath);
            CoverageSummaryData fileSummary = new CoverageSummaryData(uriPath, fileData);
            files.add(fileSummary);
            totals.add(fileSummary);
        }
        Collections.sort(files, sorter.byStatementCoverageDesc());
    }

    public List<CoverageSummaryData> getFiles() {
        return files;
    }

    public CoverageSummaryData getTotals() {
        return totals;
    }
}
