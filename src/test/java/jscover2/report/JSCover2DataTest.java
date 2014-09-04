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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class JSCover2DataTest {
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
    public void shouldCreateCoverageObjectFromJSON() throws Exception {
        String code = "function sq(x){return x*x;}";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover");
        JSCover2Data data = new JSCover2Data(json);
        assertThat(data.getDataMap().size(), is(1));
        assertThat(data.getDataMap().keySet(), hasItem("test.js"));
   }

    @Test
    public void shouldCreateCoverageObjectFromJSONWithTwoFiles() throws Exception {
        String code1 = "function sq(x){return x*x;}";
        String instrumented1 = instrumenter.instrument("test1.js", code1);
        engine.eval(instrumented1);
        String code2 = "sq(5);";
        String instrumented2 = instrumenter.instrument("test2.js", code2);
        engine.eval(instrumented2);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover");
        JSCover2Data data = new JSCover2Data(json);
        assertThat(data.getDataMap().size(), is(2));
        assertThat(data.getDataMap().keySet(), hasItem("test1.js"));
        assertThat(data.getDataMap().keySet(), hasItem("test2.js"));
   }
}