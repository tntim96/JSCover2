package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SwitchTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCoverEmptySwitch() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 1;switch(x) {}");
        engine.eval(instrumented);
        assertThat(engine.eval("x"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), nullValue());
    }

    @Test
    public void shouldCoverSwitchPath1() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 1;switch(x) {\n  case 1: x = \"one\"; break;\n  case 2: x = \"two\"; break;\n}");
        assertThat(engine.eval(instrumented), equalTo("one"));
        assertThat(engine.eval("x"), equalTo("one"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
    }

    @Test
    public void shouldCoverSwitchPath2() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 2;switch(x) {\n  case 1: x = \"one\"; break;\n  case 2: x = \"two\"; break;\n}");
        assertThat(engine.eval(instrumented), equalTo("two"));
        assertThat(engine.eval("x"), equalTo("two"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
    }

    @Test
    public void shouldCoverSwitchDefaultPath() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 4;switch(x) {\n  case 1: x = \"one\"; break;\n  default: x = \"other\"; break;\n}");
        assertThat(engine.eval(instrumented), equalTo("other"));
        assertThat(engine.eval("x"), equalTo("other"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].sD)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":10}},\"2\":{\"pos\":{\"line\":2,\"col\":10,\"len\":10}},\"3\":{\"pos\":{\"line\":3,\"col\":11,\"len\":12}}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].bD)"), equalTo("{}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD)"), equalTo("{}"));
    }
}
