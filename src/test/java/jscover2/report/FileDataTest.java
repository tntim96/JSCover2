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

public class FileDataTest {
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
        FileData fileData = new FileData(json);
        assertThat(fileData.getStatements().size(), is(2));
        assertThat(fileData.getStatements().get(0).getHits(), is(1));
        assertThat(fileData.getStatements().get(0).getPosition().getLine(), is(1));
        assertThat(fileData.getStatements().get(0).getPosition().getColumn(), is(0));
        assertThat(fileData.getStatements().get(0).getPosition().getLength(), is(34));
        assertThat(fileData.getStatements().get(1).getHits(), is(0));
        assertThat(fileData.getStatements().get(1).getPosition().getLine(), is(2));
        assertThat(fileData.getStatements().get(1).getPosition().getColumn(), is(2));
        assertThat(fileData.getStatements().get(1).getPosition().getLength(), is(13));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getStatements().get(0).getHits(), is(1));
        assertThat(fileData.getStatements().get(1).getHits(), is(1));
    }
}