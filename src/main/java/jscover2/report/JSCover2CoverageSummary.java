package jscover2.report;

import java.util.HashMap;
import java.util.Map;

public class JSCover2CoverageSummary {
    private Map<String, CoverageSummaryData> map = new HashMap<>();
    private CoverageSummaryData summary = new CoverageSummaryData();

    private JSCover2CoverageSummary() {}

    public JSCover2CoverageSummary(JSCover2Data data) {
        for (String uriPath : data.getDataMap().keySet()) {
            FileData fileData = data.getDataMap().get(uriPath);
            CoverageSummaryData fileSummary = new CoverageSummaryData(fileData);
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
