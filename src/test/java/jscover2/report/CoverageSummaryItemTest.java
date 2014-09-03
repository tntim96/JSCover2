package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CoverageSummaryItemTest {
    @Test
    public void shouldCalculateCoverageRatio() {
        CoverageSummaryItem coverageSummaryItem = new CoverageSummaryItem(100, 25);
        assertThat(coverageSummaryItem.getRatio(), equalTo(.25f));
    }
}