package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CoverageItemTest {
    @Test
    public void shouldCalculateCoverageRatio() {
        CoverageItem coverageItem = new CoverageItem(100, 25);
        assertThat(coverageItem.getRatio(), equalTo(.25f));
    }
}