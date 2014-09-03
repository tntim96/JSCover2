package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jscover2.instrument.Configuration;
import jscover2.instrument.Instrumenter;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CoverageDataTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws ScriptException {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCalculateStatementAndLineCoverage() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {\n  return x * x;\n}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        CoverageData coverageData = new CoverageData(json);
        assertThat(coverageData.getStatements().getRatio(), is(0.5f));
        assertThat(coverageData.getLines().getRatio(), is(0.5f));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageData = new CoverageData(json);
        assertThat(coverageData.getStatements().getRatio(), is(1.0f));
        assertThat(coverageData.getLines().getRatio(), is(1.0f));
    }

    @Test
    public void shouldCalculateStatementAndLineCoverageOnSingleLine() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {  return x * x;}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        CoverageData coverageData = new CoverageData(json);
        assertThat(coverageData.getStatements().getRatio(), is(0.5f));
        assertThat(coverageData.getLines().getRatio(), is(1f));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageData = new CoverageData(json);
        assertThat(coverageData.getStatements().getRatio(), is(1.0f));
        assertThat(coverageData.getLines().getRatio(), is(1.0f));
    }
}