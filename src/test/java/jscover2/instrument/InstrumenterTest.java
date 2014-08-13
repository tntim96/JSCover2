package jscover2.instrument;

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
    public void shouldRecordStatement() throws ScriptException {
        String instrumented = instrumenter.instrument("x = 1;");
        //System.out.println("instrumented = " + instrumented);
        assertThat(engine.eval(instrumented), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
    }
}