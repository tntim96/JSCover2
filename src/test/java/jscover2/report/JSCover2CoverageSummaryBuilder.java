package jscover2.report;

import jscover2.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JSCover2CoverageSummaryBuilder {
    private Comparator<CoverageSummaryData> comparator;
    private List<CoverageSummaryData> files = new ArrayList<>();
    private CoverageSummaryData totals = new CoverageSummaryData();

    public JSCover2CoverageSummaryBuilder withSorter(Comparator<CoverageSummaryData> comparator) {
        this.comparator = comparator;
        return this;
    }

    public JSCover2CoverageSummaryBuilder withTotal(CoverageSummaryData data) {
        this.totals = data;
        return this;
    }

    public JSCover2CoverageSummaryBuilder withFileData(CoverageSummaryData summary) {
        this.files.add(summary);
        return this;
    }

    public JSCover2CoverageSummary build() {
        JSCover2CoverageSummary coverageSummary = ReflectionUtils.newInstance(JSCover2CoverageSummary.class);
        ReflectionUtils.setField(coverageSummary, "files", files);
        ReflectionUtils.setField(coverageSummary, "totals", totals);
        if (comparator != null)
            Collections.sort(files, comparator);
        return coverageSummary;
    }
}
