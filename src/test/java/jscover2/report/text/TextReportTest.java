package jscover2.report.text;

import jscover2.report.CoverageSummaryData;
import jscover2.report.CoverageSummaryDataBuilder;
import jscover2.report.JSCover2CoverageSummary;
import jscover2.report.JSCover2CoverageSummaryBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TextReportTest {
    private TextFormat textFormat = new TextFormat();
    private CoverageSummaryData data = new CoverageSummaryDataBuilder()
            .withStatementCoverage(20, 30)
            .withLineCoverage(7, 8)
            .withFunctionCoverage(3, 3)
            .withBranchPath(12, 15)
            .withBooleanExpressionsCoverage(19, 25)
            .build();

    @Test
    public void shouldFormatSummaryResultsInTable() {
        JSCover2CoverageSummary summary = new JSCover2CoverageSummaryBuilder()
                .withData(data)
                .build();

        String report = textFormat.getTableFormattedSummary(summary);
        String expected =
                "Coverage type       Hits Total     %\n" +
                "Statements            20    30  66.7\n" +
                "Lines                  7     8  87.5\n" +
                "Functions              3     3 100.0\n" +
                "Branches              12    15  80.0\n" +
                "Boolean Expressions   19    25  76.0\n";
        assertThat(report, equalTo(expected));
    }

    @Test
    public void shouldFormatFileSummaryResultsInTable() {
        CoverageSummaryData data2 = new CoverageSummaryDataBuilder()
                .withStatementCoverage(19, 30)
                .withLineCoverage(6, 8)
                .withFunctionCoverage(2, 3)
                .withBranchPath(11, 15)
                .withBooleanExpressionsCoverage(17, 25)
                .build();
        CoverageSummaryData data3 = new CoverageSummaryDataBuilder()
                .withStatementCoverage(21, 30)
                .withLineCoverage(8, 8)
                .withFunctionCoverage(3, 3)
                .withBranchPath(13, 15)
                .withBooleanExpressionsCoverage(19, 25)
                .build();
        JSCover2CoverageSummary summary = new JSCover2CoverageSummaryBuilder()
                .withFileData("file1", data)
                .withFileData("file2", data2)
                .withFileData("file3", data3)
                .build();

        String report = textFormat.getTableFormattedFileSummary(summary);
        String expected =
                "URI   |          Statements |               Lines |           Functions |            Branches | Boolean Expressions\n" +
                "file1 |    20 / 30     66.7 |     7 / 8      87.5 |     3 / 3     100.0 |    12 / 15     80.0 |    19 / 25     76.0\n" +
                "file2 |    19 / 30     63.3 |     6 / 8      75.0 |     2 / 3      66.7 |    11 / 15     73.3 |    17 / 25     68.0\n" +
                "file3 |    21 / 30     70.0 |     8 / 8     100.0 |     3 / 3     100.0 |    13 / 15     86.7 |    19 / 25     76.0\n";
        assertThat(report, equalTo(expected));
    }
}
