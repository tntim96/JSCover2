package jscover2.instrument;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jscover2.report.JSCover2Data;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SwitchTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
    }

    @Test
    public void shouldCoverEmptySwitch() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 1;switch(x) {}");
        engine.eval(instrumented);
        assertThat(engine.eval("x"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), nullValue());
    }

    @Test
    public void shouldCoverSwitchPath1() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 1;switch(x) {\n  case 1: x = \"one\"; break;\n  case 2: x = \"two\"; break;\n}");
        assertThat(engine.eval(instrumented), equalTo("one"));
        assertThat(engine.eval("x"), equalTo("one"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
    }

    @Test
    public void shouldCoverSwitchPath2() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 2;switch(x) {\n  case 1: x = \"one\"; break;\n  case 2: x = \"two\"; break;\n}");
        assertThat(engine.eval(instrumented), equalTo("two"));
        assertThat(engine.eval("x"), equalTo("two"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
    }

    @Test
    public void shouldCoverSwitchDefaultPath() throws ScriptException {
        String instrumented = instrumenter.instrument("test.js", "var x = 4;switch(x) {\n  case 1: x = \"one\"; break;\n  default: x = \"other\"; break;\n}");
        assertThat(engine.eval(instrumented), equalTo("other"));
        assertThat(engine.eval("x"), equalTo("other"));
        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['4']"), nullValue());
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].sM)"), equalTo("{\"1\":{\"pos\":{\"line\":1,\"col\":0,\"len\":10}},\"2\":{\"pos\":{\"line\":2,\"col\":10,\"len\":10}},\"3\":{\"pos\":{\"line\":3,\"col\":11,\"len\":12}}}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].beM)"), equalTo("{}"));
        assertThat(engine.eval("JSON.stringify(jscover['test.js'].fM)"), equalTo("{}"));
    }

    @Test
    public void shouldCoverSwitchFallThroughPath() throws Exception {
        String code = "function sw(x) {\nvar y; switch(x) {\ncase 1:\ncase 2:\ncase 3: return y = 'three'; break;\ncase 4: \nreturn y;\n}\n}";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
        assertThat(invocable.invokeFunction("sw", 1), equalTo("three"));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][2]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][3]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['2']"), nullValue());

        assertThat(invocable.invokeFunction("sw", 3), equalTo("three"));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][2]"), equalTo(2));
        assertThat(engine.eval("jscover['test.js'].b['1'][3]"), equalTo(0));
    }

    @Test
    public void shouldCoverTwoBranches() throws Exception {
        String code = "function sw1(x) {\nvar y; switch(x) {\ncase 1:\ncase 2:\ncase 3: return y = 'three'; break;\ncase 4: \nreturn y;\n}\n}\n"
        + "function sw2(x) {\nvar y; switch(x) {default: y = 10}\nreturn y;\n}";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
        assertThat(invocable.invokeFunction("sw1", 1), equalTo("three"));
        assertThat(engine.eval("jscover['test.js'].b['1'][0]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][1]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][2]"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].b['1'][3]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['1'][4]"), nullValue());
        assertThat(engine.eval("jscover['test.js'].b['2'][0]"), equalTo(0));
        assertThat(engine.eval("jscover['test.js'].b['2'][1]"), nullValue());
        assertThat(engine.eval("jscover['test.js'].b['3']"), nullValue());

        assertThat(engine.eval("jscover['test.js'].bM['1'][0].pos.line"), equalTo(3));
        assertThat(engine.eval("jscover['test.js'].bM['2'][0].pos.line"), equalTo(11));
        assertThat(engine.eval("jscover['test.js'].bM['1'][4]"), nullValue());
        assertThat(engine.eval("jscover['test.js'].bM['2'][1]"), nullValue());
        assertThat(engine.eval("jscover['test.js'].bM['3']"), nullValue());

        assertThat(invocable.invokeFunction("sw2", "anything"), equalTo(10));
        assertThat(engine.eval("jscover['test.js'].b['2'][0]"), equalTo(1));
    }
}
