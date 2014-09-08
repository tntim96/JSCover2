package jscover2.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class JSCover2CoverageSummaryTest {
    @Mock private PositionData positionData;

    @Test
    public void shouldCalculateSummaryAndPutInMap() {
        FileData data1 = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData))
                .build();
        FileData data2 = new FileDataBuilder()
                .withStatementCoverage(new CoverageData(0, positionData))
                .withStatementCoverage(new CoverageData(1, positionData))
                .withStatementCoverage(new CoverageData(1, positionData))
                .build();
        JSCover2Data jsCover2Data = new JSCover2DataBuilder()
                .withFileData("file1", data1)
                .withFileData("file2", data2)
                .build();
        JSCover2CoverageSummary summary = new JSCover2CoverageSummary(jsCover2Data);

        assertThat(summary.getTotals().getStatementCoverage().getCovered(), equalTo(3));
        assertThat(summary.getTotals().getStatementCoverage().getTotal(), equalTo(5));

        assertThat(summary.getFiles().get(0).getName(), equalTo("file2"));
        assertThat(summary.getFiles().get(0).getStatementCoverage().getCovered(), equalTo(2));
        assertThat(summary.getFiles().get(0).getStatementCoverage().getTotal(), equalTo(3));
        assertThat(summary.getFiles().get(1).getName(), equalTo("file1"));
        assertThat(summary.getFiles().get(1).getStatementCoverage().getCovered(), equalTo(1));
        assertThat(summary.getFiles().get(1).getStatementCoverage().getTotal(), equalTo(2));
    }
}