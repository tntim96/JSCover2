package jscover2.report.text;

import jscover2.report.CoverageSummaryItem;
import jscover2.report.JSCover2CoverageSummary;

import static java.lang.String.format;

public class TextFormat {
    private String headFormat = "%-19s %4s %5s %5s\n";
    private String dataFormat = "%-19s %4d %5d %5.1f\n";
    private String headings[] = {"Coverage type", "Hits", "Total", "%"};

    public String getTableFormattedSummary(JSCover2CoverageSummary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(format(headFormat, headings));
        sb.append(getReportLine("Statements", summary.getSummary().getStatementCoverage()));
        sb.append(getReportLine("Lines", summary.getSummary().getLineCoverage()));
        sb.append(getReportLine("Functions", summary.getSummary().getFunctionCoverage()));
        sb.append(getReportLine("Branches", summary.getSummary().getBranchPathCoverage()));
        sb.append(getReportLine("Boolean Expressions", summary.getSummary().getBooleanExpressionCoverage()));
        return sb.toString();
    }

    private String getReportLine(String type, CoverageSummaryItem coverage) {
        return format(dataFormat, type, coverage.getCovered(), coverage.getTotal(), coverage.getRatio() * 100);
    }
}
