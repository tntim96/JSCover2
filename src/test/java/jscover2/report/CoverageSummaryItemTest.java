package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CoverageSummaryItemTest {
    @Test
    public void shouldCalculateCoverageRatio() {
        CoverageSummaryItem coverageSummaryItem = new CoverageSummaryItem(25, 100);
        assertThat(coverageSummaryItem.getRatio(), equalTo(.25f));
    }

    @Test
    public void shouldAddItems() {
        CoverageSummaryItem item1 = new CoverageSummaryItem(20, 60);
        CoverageSummaryItem item2 = new CoverageSummaryItem(5, 40);
        item1.add(item2);
        assertThat(item1.getRatio(), equalTo(.25f));
    }
}