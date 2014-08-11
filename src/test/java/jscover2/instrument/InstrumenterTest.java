package jscover2.instrument;

import jdk.nashorn.api.scripting.JSObject;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class InstrumenterTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Instrumenter instrumenter = new Instrumenter();

    @Test
    public void shouldRecordLineCoverage() throws ScriptException {
        String instrumented = instrumenter.instrument("x = 1;");
        assertThat(engine.eval(instrumented), equalTo(1));
        String json = (String)engine.eval("JSON.stringify(jscover);");
        assertThat(json, equalTo("{\"test.js\":{\"s\":[null,1],\"f\":[],\"b\":{}}}"));
    }
}