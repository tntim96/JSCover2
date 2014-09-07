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

    @Test
    public void shouldMergeItems() {
        CoverageSummaryItem item1 = new CoverageSummaryItem(60, 20);
        CoverageSummaryItem item2 = new CoverageSummaryItem(40, 5);
        item1.merge(item2);
        assertThat(item1.getRatio(), equalTo(.25f));
    }
}