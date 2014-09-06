package jscover2.report;

import jscover2.instrument.FileDataBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CoverageSummaryDataTest {
    @Mock PositionData positionData;

    @Test
    public void shouldCalculateStatement() {
        FileData fileData = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData))
                .build();
        CoverageSummaryData summaryData = new CoverageSummaryData(fileData);
        assertThat(summaryData.getStatements().getRatio(), equalTo(0.5f));
    }
}