package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class InstrumenterTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCoverStatement() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].sD)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":6}}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].bD)"), equalTo("{}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD)"), equalTo("{}"));
    }

    @Test
    public void shouldCoverStatementTwice() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(2));
    }

    @Test
    public void shouldCoverStatementWithDifferentUrlPath() throws ScriptException {
        String instrumented = instrumenter.instrument("/diff.js", "x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['/diff.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['/diff.js'].sD['1'].pos.line"), equalTo(1));
    }

    @Test
    public void shouldCoverStatementWithDifferentCoverageVariable() throws ScriptException {
        config.setCoverVariableName("__cov__");
        instrumenter = new Instrumenter(config);
        assertThat(engine.eval(instrumenter.instrument("/diff.js", "x = 1;")), equalTo(1));
        assertThat(engine.eval("__cov__['/diff.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("__cov__['/diff.js'].sD['1'].pos.line"), equalTo(1));
    }

    @Test
    public void shouldCoverTwoStatement() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "x = 1; ++x;");
        assertThat(engine.eval(instrumented), equalTo(2.0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
    }

    @Test
    public void shouldCreateStatementData() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "\n     x =\n 1;");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].sD['1'].pos.line"), equalTo(2));
        assertThat(engine.eval("jscover['test.js'].sD['1'].pos.col"), equalTo(5));
        assertThat(engine.eval("jscover['test.js'].sD['1'].pos.len"), equalTo(7));
    }

    @Test
    public void shouldCreateTwoStatementDataSets() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "x = 1; ++x;");
        assertThat(engine.eval(instrumented), equalTo(2.0));
        assertThat(engine.eval("jscover['test.js'].sD['1'].pos.line"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].sD['1'].pos.col"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].sD['1'].pos.len"), equalTo(6));
        assertThat(engine.eval("jscover['test.js'].sD['2'].pos.line"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].sD['2'].pos.col"), equalTo(7));
        assertThat(engine.eval("jscover['test.js'].sD['2'].pos.len"), equalTo(4));
    }

    @Test
    public void shouldCoverDeclaration() throws ScriptException {
        engine.eval(instrumenter.instrument("test.js", "var x;"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
    }

    @Test
    public void shouldCoverDeclarationAndAssignmentOnce() throws ScriptException {
        String instrument = instrumenter.instrument("test.js", "var x = 1;");
        engine.eval(instrument);
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), nullValue());
    }

    @Test
    public void shouldCoverIf() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 0;\nif (x < 0)\n  x++;");
        assertThat(engine.eval(instrumented), equalTo(0.0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
    }

    @Test
    public void shouldCoverFunction() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {\n  return x*x;\n}\nsq(5);");
        assertThat(engine.eval(instrumented), equalTo(25.0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(1));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].sD)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":32}},\"2\":{\"pos\":{\"line\":2,\"col\":2,\"len\":11}},\"3\":{\"pos\":{\"line\":4,\"col\":0,\"len\":6}}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].bD)"), equalTo("{}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fD)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":32}}}"));
    }

    @Test
    public void shouldNotCoverFunction() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "function sq(x) {return x*x;}");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(0));
    }

    @Test
    public void shouldCoverIfElse() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 0;\nif (x <= 0)\n  x++;\nelse\n  x--;");
        assertThat(engine.eval(instrumented), equalTo(0.0));
        assertThat(engine.eval("x"), equalTo(1.0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(0));
    }

    @Test
    public void shouldCoverIfElseWithBrackets() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 0;\nif (x <= 0)\n {x++;} else\n {x--;}");
        assertThat(engine.eval(instrumented), equalTo(0.0));
        assertThat(engine.eval("x"), equalTo(1.0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(0));
    }

    @Test
    public void shouldCoverIfIfElseElse() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 0;\nif (x < 0)\n {x++;}\nelse if (x > 0){\n  x--;\n}\n else\n {x;}");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['5']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['6']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['2'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['2'][1]"), equalTo(1));
    }

    @Test
    public void shouldCreateBranchDataSet() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 0;\nif (x < 0)\n  x++;");
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].bD['1'].pos.line"), equalTo(2));
        assertThat(engine.eval("jscover['test.js'].bD['1'].pos.col"), equalTo(4));
        assertThat(engine.eval("jscover['test.js'].bD['1'].pos.len"), equalTo(5));
    }
}