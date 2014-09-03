package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class StatementTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        config.setIncludeFunctions(false);
        config.setIncludeBranches(false);
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCoverStatement() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].sM)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":6}}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].dM)"), equalTo("{}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fM)"), equalTo("{}"));
    }
}