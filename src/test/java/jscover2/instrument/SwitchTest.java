package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
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
        String instrumented = instrumenter.instrument("test.js", "var x = 1;switch(x) {} x;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
    }

    @Test
    public void shouldCoverSwitchPath1() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 1;switch(x) {\n  case 1: x = \"one\"; break;\n  case 2: x = \"two\"; break;\n} x;");
        assertThat(engine.eval(instrumented), equalTo("one"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(1));
    }

    @Test
    public void shouldCoverSwitchPath2() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 2;switch(x) {\n  case 1: x = \"one\"; break;\n  case 2: x = \"two\"; break;\n} x;");
        assertThat(engine.eval(instrumented), equalTo("two"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(1));
    }

    @Test
    public void shouldCoverSwitchDefaultPath() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 4;switch(x) {\n  case 1: x = \"one\"; break;\n  default: x = \"other\"; break;\n} x;");
        assertThat(engine.eval(instrumented), equalTo("other"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(1));
    }
}
