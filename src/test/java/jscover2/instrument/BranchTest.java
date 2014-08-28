package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class BranchTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws ScriptException {
        config.setCoverVariableName("jscover");
        config.setExcludeConditions(true);
        instrumenter = new Instrumenter(config);
        String code = "function condition(a, b, c) {\n" +
                "    if ((a || b) && c)\n" +
                "        return true;\n" +
                "    return false;\n" +
                "}\n";
        engine.eval(instrumenter.instrument("test.js", code));
    }

    @Test
    public void shouldCoverBranchPath1() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));

        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['5']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].bD['1'].br"), equalTo("true"));
        assertThat(engine.eval("jscover['test.js'].bD['2']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(0));
    }

    @Test
    public void shouldCoverBranchPath2() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("condition", false, true, false), is(false));

        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['5']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].bD['1'].br"), equalTo("true"));
        assertThat(engine.eval("jscover['test.js'].bD['2']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
    }
}
