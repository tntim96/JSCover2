package jscover2.instrument;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JavaScriptFilesTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws ScriptException {
        config.setCoverVariableName("jscover");
        config.setIncludeFunctions(false);
        config.setIncludeBranches(false);
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCreateLineCoverageEntry() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "x = 1;");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        engine.eval(new FileReader("src/main/resources/jscover.js"));
        assertThat(json.containsKey("l"), is(false));
        invocable.invokeFunction("calculateLineCoverage", json);
        assertThat(json.containsKey("l"), is(true));
    }
}
