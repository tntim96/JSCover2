package jscover2.report;

import jscover2.utils.ReflectionUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class JSCover2CoverageSummaryBuilder {
    private SortedMap<String, CoverageSummaryData> map = new TreeMap<>();
    private CoverageSummaryData summary = new CoverageSummaryData();

    public JSCover2CoverageSummaryBuilder withData(CoverageSummaryData summary) {
        this.summary = summary;
        return this;
    }

    public JSCover2CoverageSummaryBuilder withFileData(String uriPath, CoverageSummaryData summary) {
        this.map.put(uriPath, summary);
        return this;
    }

    public JSCover2CoverageSummary build() {
        JSCover2CoverageSummary coverageSummary = ReflectionUtils.newInstance(JSCover2CoverageSummary.class);
        ReflectionUtils.setField(coverageSummary, "map", map);
        ReflectionUtils.setField(coverageSummary, "summary", summary);
        return coverageSummary;
    }
}
