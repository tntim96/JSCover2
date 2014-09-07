package jscover2.report.text;

import jscover2.report.CoverageSummaryData;
import jscover2.report.CoverageSummaryItem;
import jscover2.report.JSCover2CoverageSummary;

import static java.lang.String.format;

public class TextFormat {
    private String headFormat = "%-19s %4s %5s %5s\n";
    private String dataFormat = "%-19s %4d %5d %5.1f\n";
    private String headings[] = {"Coverage type", "Hits", "Total", "%"};
    private String statElementFormat = " | %5d / %-5d %5.1f";

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

    public String getTableFormattedFileSummary(JSCover2CoverageSummary summary) {
        int maxLength = getMaximumUriPathLength(summary);
        String fileHeadFormat = "%-"+maxLength+"s | %19s | %19s | %19s | %19s | %19s\n";
        String fileHeadings[] = {"URI", "Statements", "Lines", "Functions", "Branches", "Boolean Expressions"};
        String uriPathFormat = "%-" + maxLength + "s";

        StringBuilder sb = new StringBuilder();
        sb.append(format(fileHeadFormat, fileHeadings));
        for (String uriPath : summary.getMap().keySet()) {
            sb.append(format(uriPathFormat, uriPath));
            appendCoverageData(sb, summary.getMap().get(uriPath));
        }
        return sb.toString();
    }

    private void appendCoverageData(StringBuilder sb, CoverageSummaryData data) {
        sb.append(formatStatElement(data.getStatementCoverage()));
        sb.append(formatStatElement(data.getLineCoverage()));
        sb.append(formatStatElement(data.getFunctionCoverage()));
        sb.append(formatStatElement(data.getBranchPathCoverage()));
        sb.append(formatStatElement(data.getBooleanExpressionCoverage()));
        sb.append("\n");
    }

    private String formatStatElement(CoverageSummaryItem data) {
        return format(statElementFormat, data.getCovered(), data.getTotal(), data.getRatio()*100);
    }

    private int getMaximumUriPathLength(JSCover2CoverageSummary summary) {
        int maxLength = 0;
        for (String uriPath : summary.getMap().keySet())
            if (uriPath.length() > maxLength)
                maxLength = uriPath.length();
        return maxLength;
    }
}
