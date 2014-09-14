package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LineCompleteDataTest {
    private LineCompleteData data = new LineCompleteData();

    @Test
    public void shouldDetectLineHit() {
        data.addStatement(new CoverageData(12, PositionDataBuilder.getPositionData(1, 1, 1)));
        assertThat(data.hitLine(), is(true));
        assertThat(data.getLineHits(), is(12));
    }

    @Test
    public void shouldDetectLineMiss() {
        data.addStatement(new CoverageData(0, PositionDataBuilder.getPositionData(1, 1, 1)));
        data.addStatement(new CoverageData(0, PositionDataBuilder.getPositionData(1, 1, 1)));
        assertThat(data.hitLine(), is(false));
        assertThat(data.getLineHits(), is(0));
    }

    @Test
    public void shouldDetectBranchHit() {
        data.addBranch(new CoverageData(12, PositionDataBuilder.getPositionData(1, 1, 1)));
        assertThat(data.isBranchMissed(), is(false));
        assertThat(data.getLineHits(), is(12));
    }

    @Test
    public void shouldDetectBranchMissed() {
        data.addBranch(new CoverageData(12, PositionDataBuilder.getPositionData(1, 1, 1)));
        data.addBranch(new CoverageData(0, PositionDataBuilder.getPositionData(1, 20, 1)));
        assertThat(data.isBranchMissed(), is(true));
        assertThat(data.getLineHits(), is(12));
    }
}