package jscover2.report;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JSCover2CoverageSummary {
    private SortedMap<String, CoverageSummaryData> map = new TreeMap<>();
    private CoverageSummaryData summary = new CoverageSummaryData();

    private JSCover2CoverageSummary() {}

    public JSCover2CoverageSummary(JSCover2Data data) {
        for (String uriPath : data.getDataMap().keySet()) {
            FileData fileData = data.getDataMap().get(uriPath);
            CoverageSummaryData fileSummary = new CoverageSummaryData(uriPath, fileData);
            map.put(uriPath, fileSummary);
            summary.add(fileSummary);
        }
    }

    public Map<String, CoverageSummaryData> getMap() {
        return map;
    }

    public CoverageSummaryData getSummary() {
        return summary;
    }
}
