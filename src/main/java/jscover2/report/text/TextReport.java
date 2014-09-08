package jscover2.report.text;

import jscover2.report.CoverageSummaryData;
import jscover2.report.CoverageSummaryItem;
import jscover2.report.JSCover2CoverageSummary;

import static java.lang.String.format;

public class TextReport {
    private String headFormat = "%-19s %4s %5s %5s\n";
    private String dataFormat = "%-19s %4d %5d %5.1f\n";
    private String headings[] = {"Coverage type", "Hits", "Total", "%"};

    public String getTableFormattedSummary(JSCover2CoverageSummary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(format(headFormat, headings));
        sb.append(getReportLine("Statements", summary.getTotals().getStatementCoverage()));
        sb.append(getReportLine("Lines", summary.getTotals().getLineCoverage()));
        sb.append(getReportLine("Functions", summary.getTotals().getFunctionCoverage()));
        sb.append(getReportLine("Branches", summary.getTotals().getBranchPathCoverage()));
        sb.append(getReportLine("Boolean Expressions", summary.getTotals().getBooleanExpressionCoverage()));
        return sb.toString();
    }

    private String getReportLine(String type, CoverageSummaryItem coverage) {
        return format(dataFormat, type, coverage.getCovered(), coverage.getTotal(), coverage.getRatio() * 100);
    }

    public String getTableFormattedFileSummary(JSCover2CoverageSummary summary) {
        int maxLength = getMaximumUriPathLength(summary);
        String formats[] = getFormats(maxLength, summary);
        String fileHeadFormat = formats[0];
        String fileDataFormat = formats[1];
        String fileHeadings[] = {"URI", "Statement", "Line", "Function", "Branch", "Bool Expr"};

        StringBuilder sb = new StringBuilder();
        sb.append(format(fileHeadFormat, fileHeadings));

        CoverageSummaryData totals = summary.getTotals();
        appendCoverageData(sb, fileDataFormat, totals);
        for (CoverageSummaryData data : summary.getFiles())
            appendCoverageData(sb, fileDataFormat, data);
        return sb.toString();
    }

    private String[] getFormats(int maxNameLength, JSCover2CoverageSummary summary) {
        String headers[] = new String[2];
        StringBuilder headerFormat = new StringBuilder("%-" + maxNameLength + "s ");
        StringBuilder dataFormat = new StringBuilder("%-" + maxNameLength + "s ");
        addFormatElement(headerFormat, dataFormat, summary.getTotals().getStatementCoverage());
        addFormatElement(headerFormat, dataFormat, summary.getTotals().getLineCoverage());
        addFormatElement(headerFormat, dataFormat, summary.getTotals().getFunctionCoverage());
        addFormatElement(headerFormat, dataFormat, summary.getTotals().getBranchPathCoverage());
        addFormatElement(headerFormat, dataFormat, summary.getTotals().getBooleanExpressionCoverage());
        headerFormat.append("\n");
        dataFormat.append("\n");
        headers[0] = headerFormat.toString();
        headers[1] = dataFormat.toString();
        return headers;
    }

    private void addFormatElement(StringBuilder headerFormat, StringBuilder dataFormat, CoverageSummaryItem coverage) {
        int coverWidth = ("" + coverage.getCovered()).length();
        int totalWidth = ("" + coverage.getTotal()).length();
        headerFormat.append("|%" + (coverWidth + totalWidth + 7)+"s");
        dataFormat.append("|%" + coverWidth + "d/%-" + totalWidth + "d %5.1f");
    }

    private void appendCoverageData(StringBuilder sb, String fileDataFormat, CoverageSummaryData data) {
        sb.append(format(fileDataFormat
                , data.getName()
                , data.getStatementCoverage().getCovered()
                , data.getStatementCoverage().getTotal()
                , data.getStatementCoverage().getRatio()*100
                , data.getLineCoverage().getCovered()
                , data.getLineCoverage().getTotal()
                , data.getLineCoverage().getRatio()*100
                , data.getFunctionCoverage().getCovered()
                , data.getFunctionCoverage().getTotal()
                , data.getFunctionCoverage().getRatio()*100
                , data.getBranchPathCoverage().getCovered()
                , data.getBranchPathCoverage().getTotal()
                , data.getBranchPathCoverage().getRatio()*100
                , data.getBooleanExpressionCoverage().getCovered()
                , data.getBooleanExpressionCoverage().getTotal()
                , data.getBooleanExpressionCoverage().getRatio()*100));
    }

    private int getMaximumUriPathLength(JSCover2CoverageSummary summary) {
        int maxLength = 0;
        for (CoverageSummaryData data : summary.getFiles())
            if (data.getName().length() > maxLength)
                maxLength = data.getName().length();
        return maxLength;
    }
}
