package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LineCompleteDataTest {
    private LineCompleteData data = new LineCompleteData();

    @Test
    public void shouldDetectHit() {
        data.addStatement(new CoverageData(1, PositionDataBuilder.getPositionData(1, 1, 1)));
        assertThat(data.hit(), is(true));
    }

    @Test
    public void shouldNotDetectHit() {
        data.addStatement(new CoverageData(0, PositionDataBuilder.getPositionData(1, 1, 1)));
        assertThat(data.hit(), is(false));
    }
}