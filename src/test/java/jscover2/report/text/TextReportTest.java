package jscover2.report.text;

import jscover2.report.*;
import org.junit.Test;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class TextReportTest {
    private TextFormat textFormat = new TextFormat();

    @Test
    public void shouldFormatResultsInTable() {
        CoverageSummaryData data= new CoverageSummaryDataBuilder()
                .withStatementCoverage(20, 30)
                .withLineCoverage(7, 8)
                .withFunctionCoverage(3, 3)
                .withBranchPath(12, 15)
                .withBooleanExpressionsCoverage(19, 25)
                .build();
        JSCover2CoverageSummary summary = new JSCover2CoverageSummaryBuilder()
                .withCoverageSummaryData(data)
                .build();

        String report = textFormat.getTableFormattedSummary(summary);
        assertThat(report, containsString("Coverage type       Hits Total     %\n"));
        assertThat(report, containsString("Statements            20    30  66.7\n"));
        assertThat(report, containsString("Lines                  7     8  87.5\n"));
        assertThat(report, containsString("Functions              3     3 100.0\n"));
        assertThat(report, containsString("Branches              12    15  80.0\n"));
        assertThat(report, containsString("Boolean Expressions   19    25  76.0\n"));
    }
}
