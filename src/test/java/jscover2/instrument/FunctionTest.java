package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FunctionTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;
    private Invocable invocable = (Invocable) engine;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        config.setIncludeStatements(false);
        config.setIncludeBranches(false);
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldNotCoverFunction() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {\n  return x*x;\n};");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(0));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":32}}}"));
    }

    @Test
    public void shouldCoverFunction() throws ScriptException, NoSuchMethodException {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {\n  return x*x;\n};");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(0));
        assertThat(invocable.invokeFunction("sq", 2), equalTo(4.0));
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(invocable.invokeFunction("sq", 5), equalTo(25.0));
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(2));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":32}}}"));
    }

    @Test
    public void shouldHandleTwoFunctions() throws ScriptException, NoSuchMethodException {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {\n  return x*x;\n};" +
                "function cube(x) {\n  return x*x*x;\n};\n");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].f['2']"), equalTo(0));
        assertThat(invocable.invokeFunction("sq", 2), equalTo(4.0));
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].f['2']"), equalTo(0));
        assertThat(invocable.invokeFunction("cube", 3), equalTo(27.0));
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].f['2']"), equalTo(1));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD['1'])"), equalTo("{\"pos\":{\"line\":1,\"col\":0,\"len\":32}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD['2'])"), equalTo("{\"pos\":{\"line\":3,\"col\":2,\"len\":36}}"));
    }
}