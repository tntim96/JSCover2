package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BooleanExpressionDataTest {
    @Test
    public void shouldDetectHit() {
        assertThat(new BooleanExpressionData(1, 1, null, false).hit(), is(true));
    }

    @Test
    public void shouldNotDetectHit() {
        assertThat(new BooleanExpressionData(0, 0, null, false).hit(), is(false));
        assertThat(new BooleanExpressionData(10, 0, null, false).hit(), is(false));
        assertThat(new BooleanExpressionData(0, 10, null, false).hit(), is(false));
    }
}