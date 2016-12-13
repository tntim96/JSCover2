package jscover2.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CoverageSummaryDataTest {
    @Mock PositionData positionData;
    private String uriPath = "test.js";

    @Test
    public void shouldCalculateStatementCoverage() {
        FileData fileData = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getStatementCoverage().getRatio(), equalTo(0.5f));
    }

    @Test
    public void shouldCalculateLineCoverage() {
        PositionData positionData2 = mock(PositionData.class);
        FileData fileData = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData2))
                .build();

        given(positionData.getLine()).willReturn(1);
        given(positionData2.getLine()).willReturn(2);

        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);

        assertThat(summaryData.getLineCoverage().getRatio(), equalTo(0.5f));
    }

    @Test
    public void shouldCalculateLineCoverageWithStatementsOnSameLine() {
        PositionData positionData2 = mock(PositionData.class);
        FileData fileData = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData2))
                .build();

        given(positionData.getLine()).willReturn(1);
        given(positionData2.getLine()).willReturn(1);

        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);

        assertThat(summaryData.getLineCoverage().getRatio(), equalTo(1f));
    }

    @Test
    public void shouldCalculateFunctionCoverage() {
        FileData fileData = new FileDataBuilder()
                .withFunctionCoverage(new CoverageData(0, null))
                .withFunctionCoverage(new CoverageData(1, null))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getFunctionCoverage().getRatio(), equalTo(0.5f));
    }

    @Test
    public void shouldCalculateBooleanExpressionCoverage() {
        FileData fileData = new FileDataBuilder()
                .withBooleanExpressionsCoverage(new BooleanExpressionData(0, 0, null, false))
                .withBooleanExpressionsCoverage(new BooleanExpressionData(1, 1, null, false))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getBooleanExpressionCoverage().getRatio(), equalTo(0.5f));
    }

    @Test
    public void shouldCalculateBooleanExpressionCoverageWithPartiallyEvaluatedPaths() {
        FileData fileData = new FileDataBuilder()
                .withBooleanExpressionsCoverage(new BooleanExpressionData(0, 1, null, false))
                .withBooleanExpressionsCoverage(new BooleanExpressionData(1, 0, null, false))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getBooleanExpressionCoverage().getRatio(), equalTo(0f));
    }

    @Test
    public void shouldCalculateBranchPathCoverageWithBoolean() {
        FileData fileData = new FileDataBuilder()
                .withBooleanBranch(new BooleanExpressionData(0, 0, null, false))
                .withBooleanBranch(new BooleanExpressionData(0, 1, null, false))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getBranchPathCoverage().getRatio(), equalTo(0.25f));
    }

    @Test
    public void shouldCalculateBranchPathCoverageWithBranch() {
        FileData fileData = new FileDataBuilder()
                .withBranchPath(new CoverageData(0, positionData))
                .withBranchPath(new CoverageData(1, positionData))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getBranchPathCoverage().getRatio(), equalTo(0.5f));
    }

    @Test
    public void shouldCalculateBranchPathCoverageWithBooleanAndBranch() {
        FileData fileData = new FileDataBuilder()
                .withBooleanBranch(new BooleanExpressionData(1, 0, null, false))
                .withBooleanBranch(new BooleanExpressionData(0, 1, null, false))
                .withBranchPath(new CoverageData(0, positionData))
                .withBranchPath(new CoverageData(1, positionData))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(uriPath, fileData);
        assertThat(summaryData.getBranchPathCoverage().getRatio(), equalTo(0.5f));
    }

    @Test
    public void shouldAdd() {
        CoverageSummaryData data1 = new CoverageSummaryDataBuilder()
                .withStatementCoverage(1, 3)
                .withLineCoverage(2, 6)
                .withFunctionCoverage(10, 30)
                .withBranchPathCoverage(100, 300)
                .withBooleanExpressionCoverage(1000, 3000)
                .build();
        CoverageSummaryData data2 = new CoverageSummaryDataBuilder()
                .withStatementCoverage(4, 7)
                .withLineCoverage(8, 14)
                .withFunctionCoverage(40, 70)
                .withBranchPathCoverage(400, 700)
                .withBooleanExpressionCoverage(4000, 7000)
                .build();

        data1.add(data2);
        assertThat(data1.getStatementCoverage().getCovered(), equalTo(5));
        assertThat(data1.getStatementCoverage().getTotal(), equalTo(10));
        assertThat(data1.getStatementCoverage().getRatio(), equalTo(0.5f));
        assertThat(data1.getLineCoverage().getCovered(), equalTo(10));
        assertThat(data1.getLineCoverage().getTotal(), equalTo(20));
        assertThat(data1.getLineCoverage().getRatio(), equalTo(0.5f));
        assertThat(data1.getFunctionCoverage().getCovered(), equalTo(50));
        assertThat(data1.getFunctionCoverage().getTotal(), equalTo(100));
        assertThat(data1.getFunctionCoverage().getRatio(), equalTo(0.5f));
        assertThat(data1.getBranchPathCoverage().getCovered(), equalTo(500));
        assertThat(data1.getBranchPathCoverage().getTotal(), equalTo(1000));
        assertThat(data1.getBranchPathCoverage().getRatio(), equalTo(0.5f));
        assertThat(data1.getBooleanExpressionCoverage().getCovered(), equalTo(5000));
        assertThat(data1.getBooleanExpressionCoverage().getTotal(), equalTo(10000));
        assertThat(data1.getBooleanExpressionCoverage().getRatio(), equalTo(0.5f));
    }
}