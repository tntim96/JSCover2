package jscover2.report;

import java.util.Comparator;

public class CoverageSummaryDataSorter {
    public Comparator<CoverageSummaryData> byName() {
        return new Comparator<CoverageSummaryData>() {
            @Override
            public int compare(CoverageSummaryData o1, CoverageSummaryData o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
    }

    public Comparator<CoverageSummaryData> byStatementCoverageDesc() {
        return new Comparator<CoverageSummaryData>() {
            @Override
            public int compare(CoverageSummaryData o1, CoverageSummaryData o2) {
                if (o1.getStatementCoverage().getRatio() < o2.getStatementCoverage().getRatio())
                    return 1;
                else if (o1.getStatementCoverage().getRatio() > o2.getStatementCoverage().getRatio())
                    return -1;
                return 0;
            }
        };
    }
}
