package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ForTest {
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
        String instrumented = instrumenter.instrument("test.js", "var j = 0;\nfor (var i = 0; i < 2; i++)\n  j++;\nj;");
        assertThat(engine.eval(instrumented), equalTo(2.0));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(2));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
    }
}
