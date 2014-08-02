package jscover2.instrument;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class NashornTest {

    @Test
    public void shouldEvaluateScript() throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        Double result = (Double)engine.eval("function sq(x){return x*x;}\nsq(3);");

        assertThat(result, equalTo(9.0));
    }

}