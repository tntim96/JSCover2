package jscover2.report;

import java.util.Comparator;

class CoverageSummaryDataSorter {
    public Comparator<CoverageSummaryData> byName() {
        return new Comparator<CoverageSummaryData>() {
            @Override
            public int compare(CoverageSummaryData o1, CoverageSummaryData o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
    }
}
