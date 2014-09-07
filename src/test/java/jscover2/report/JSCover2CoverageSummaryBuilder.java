package jscover2.report;

import jscover2.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

public class JSCover2CoverageSummaryBuilder {
    private Map<String, CoverageSummaryData> map = new HashMap<>();
    private CoverageSummaryData summary = new CoverageSummaryData();

    public JSCover2CoverageSummaryBuilder withCoverageSummaryData(CoverageSummaryData summary) {
        this.summary = summary;
        return this;
    }

    public JSCover2CoverageSummary build() {
        JSCover2CoverageSummary coverageSummary = ReflectionUtils.newInstance(JSCover2CoverageSummary.class);
        ReflectionUtils.setField(coverageSummary, "map", map);
        ReflectionUtils.setField(coverageSummary, "summary", summary);
        return coverageSummary;
    }
}
