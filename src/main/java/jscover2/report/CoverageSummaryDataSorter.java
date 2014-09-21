package jscover2.report;

import java.util.Comparator;

public class CoverageSummaryDataSorter {
    public Comparator<CoverageSummaryData> byNameDesc() {
        return byName(false);
    }

    public Comparator<CoverageSummaryData> byNameAsc() {
        return byName(true);
    }

    private Comparator<CoverageSummaryData> byName(final boolean ascending) {
        return new Comparator<CoverageSummaryData>() {
            @Override
            public int compare(CoverageSummaryData o1, CoverageSummaryData o2) {
                if (ascending)
                    return o1.getName().compareTo(o2.getName());
                else
                    return o2.getName().compareTo(o1.getName());
            }
        };
    }

    public Comparator<CoverageSummaryData> byStatementCoverageDesc() {
        return byStatementCoverage(true);
    }

    public Comparator<CoverageSummaryData> byStatementCoverageAsc() {
        return byStatementCoverage(false);
    }

    private Comparator<CoverageSummaryData> byStatementCoverage(final boolean ascending) {
        return new Comparator<CoverageSummaryData>() {
            @Override
            public int compare(CoverageSummaryData o1, CoverageSummaryData o2) {
                if (o1.getStatementCoverage().getRatio() < o2.getStatementCoverage().getRatio())
                    return ascending ? 1 : -1;
                else if (o1.getStatementCoverage().getRatio() > o2.getStatementCoverage().getRatio())
                    return ascending ? -1 : 1;
                return o1.getName().compareTo(o2.getName());
            }
        };
    }
}
