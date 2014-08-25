package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ConditionTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws ScriptException {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
        String code = "function condition(a, b, c) {\n" +
                "    return ((a || b) && c);\n" +
                "}\n";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
    }

    @Test
    public void shouldCoverCondition() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        verify(new int[]{1, 0}, new int[]{1, 0}, new int[]{1, 0}, new int[]{1, 0}, new int[]{0, 0}, 1);
    }

    @Test
    public void shouldCoverAllPaths() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        assertThat(invocable.invokeFunction("condition", false, true, false), is(false));
        assertThat(invocable.invokeFunction("condition", false, false, false), is(false));
        verify(new int[]{1, 2}, new int[]{2, 1}, new int[]{1, 2}, new int[]{1, 1}, new int[]{1, 1}, 3);
    }

    private void verify(int[] br1, int[] br2, int[] br3, int[] br4, int[] br5, int calls) throws ScriptException {
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(calls));
        assertThat(engine.eval("jscover['test.js'].s['3']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(calls));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(br1[0]));//Entire function
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(br1[1]));
        assertThat(engine.eval("jscover['test.js'].b['2'][0]"), equalTo(br2[0]));//a||b
        assertThat(engine.eval("jscover['test.js'].b['2'][1]"), equalTo(br2[1]));
        assertThat(engine.eval("jscover['test.js'].b['3'][0]"), equalTo(br3[0]));//a
        assertThat(engine.eval("jscover['test.js'].b['3'][1]"), equalTo(br3[1]));
        assertThat(engine.eval("jscover['test.js'].b['4'][0]"), equalTo(br4[0]));//c
        assertThat(engine.eval("jscover['test.js'].b['4'][1]"), equalTo(br4[1]));
        assertThat(engine.eval("jscover['test.js'].b['5'][0]"), equalTo(br5[0]));//b
        assertThat(engine.eval("jscover['test.js'].b['5'][1]"), equalTo(br5[1]));
    }
}
