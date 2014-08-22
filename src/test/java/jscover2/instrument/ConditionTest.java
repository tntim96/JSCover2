package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ConditionTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCoverCondition() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "function condition(a, b, c) {\n" +
                "    return ((a || b) && c);\n" +
                "}\n" +
                "condition(true, false, true);");
        assertThat(engine.eval(instrumented), equalTo(true));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['3'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['3'][1]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['4'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['4'][1]"), equalTo(0));//Short-circuit
        assertThat(engine.eval("jscover['test.js'].b['2'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['2'][1]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(0));
    }
}
