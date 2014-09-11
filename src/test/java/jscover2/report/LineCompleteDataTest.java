package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LineCompleteDataTest {
    private LineCompleteData data = new LineCompleteData();

    @Test
    public void shouldDetectHit() {
        data.addStatement(new CoverageData(12, PositionDataBuilder.getPositionData(1, 1, 1)));
        assertThat(data.getLineHits(), is(12));
    }
}