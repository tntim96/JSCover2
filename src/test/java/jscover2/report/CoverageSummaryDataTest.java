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

public class CoverageSummaryDataTest {
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
        CoverageSummaryData coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getStatements().getRatio(), is(0.5f));
        assertThat(coverageSummaryData.getLines().getRatio(), is(0.5f));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getStatements().getRatio(), is(1f));
        assertThat(coverageSummaryData.getLines().getRatio(), is(1f));
    }

    @Test
    public void shouldCalculateStatementAndLineCoverageOnSingleLine() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {  return x * x;}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        CoverageSummaryData coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getStatements().getRatio(), is(0.5f));
        assertThat(coverageSummaryData.getLines().getRatio(), is(1f));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getStatements().getRatio(), is(1f));
        assertThat(coverageSummaryData.getLines().getRatio(), is(1f));
    }

    @Test
    public void shouldCalculateFunctionCoverage() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x){return x*x;}" +
                "function cube(x){return x*x*x;}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        CoverageSummaryData coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getFunctions().getRatio(), is(0f));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getFunctions().getRatio(), is(0.5f));

        assertThat(invocable.invokeFunction("cube", 5), is(125.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getFunctions().getRatio(), is(1f));
    }

    @Test
    public void shouldCalculateBooleanExpressionAndBranchCoverage() throws Exception {
        String code = "function condition(a, b, c) {\n" +
                "    if ((a || b) && c)\n" +
                "        return true;\n" +
                "    else;\n" +
                "        return false;\n" +
                "}";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        CoverageSummaryData coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getBranches().getRatio(), is(0f));
        assertThat(coverageSummaryData.getBooleanExpressions().getRatio(), is(0f));

        assertThat(invocable.invokeFunction("condition", false, false, true), is(false));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getBranches().getRatio(), is(0.5f));
        assertThat(coverageSummaryData.getBooleanExpressions().getRatio(), is(0.4F));

        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getBranches().getRatio(), is(1f));
        assertThat(coverageSummaryData.getBooleanExpressions().getRatio(), is(0.8F));

        assertThat(invocable.invokeFunction("condition", false, true, false), is(false));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        coverageSummaryData = new CoverageSummaryData(json);
        assertThat(coverageSummaryData.getBranches().getRatio(), is(1f));
        assertThat(coverageSummaryData.getBooleanExpressions().getRatio(), is(1f));
    }
}