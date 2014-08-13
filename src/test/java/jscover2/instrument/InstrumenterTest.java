package jscover2.instrument;

import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class InstrumenterTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Instrumenter instrumenter = new Instrumenter();

    @Test
    public void shouldCoverStatement() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
    }

    @Test
    public void shouldCoverStatementWithDifferentUrlPath() throws ScriptException {
        String instrumented = instrumenter.instrument("test1.js", "x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test1.js'].s['1']"), equalTo(1));
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
        engine.eval(instrumented);
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
        engine.eval(instrumented);
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
    }
}