package jscover2.report;

import jscover2.instrument.FileDataBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CoverageSummaryDataTest {
    @Mock PositionData positionData;

    @Test
    public void shouldCalculateStatementCoverage() {
        FileData fileData = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(fileData);
        assertThat(summaryData.getStatements().getRatio(), equalTo(0.5f));
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

        CoverageSummaryData summaryData = new CoverageSummaryData(fileData);

        assertThat(summaryData.getLines().getRatio(), equalTo(0.5f));
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

        CoverageSummaryData summaryData = new CoverageSummaryData(fileData);

        assertThat(summaryData.getLines().getRatio(), equalTo(1f));
    }

    @Test
    public void shouldCalculateFunctionCoverage() {
        FileData fileData = new FileDataBuilder()
                .withFunctionCoverage(new CoverageData(0, null))
                .withFunctionCoverage(new CoverageData(1, null))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(fileData);
        assertThat(summaryData.getFunctions().getRatio(), equalTo(0.5f));
    }

}