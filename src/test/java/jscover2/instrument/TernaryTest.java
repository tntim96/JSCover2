package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class TernaryTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCoverTernaryConditionalPath1() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var j = j === 'undefined' ? 0 : 1;");
        engine.eval(instrumented);
        assertThat(engine.eval("j"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].be['1'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].be['1'][1]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].be['2']"), nullValue());
    }

    @Test
    public void shouldCoverTernaryConditionalPath2() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var j = j !== 'undefined' ? 0 : 1;");
        engine.eval(instrumented);
        assertThat(engine.eval("j"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].be['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].be['1'][1]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].be['2']"), nullValue());
    }

    @Test
    public void shouldCreateDataForTernaryConditional() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var j = j !== 'undefined' ? 0 : 1;");
        engine.eval(instrumented);
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].sM)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":34}}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].beM)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":8,\"len\":17},\"br\":\"true\"}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fM)"), equalTo("{}"));
    }
}
