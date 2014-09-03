package jscover2.instrument;

import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DecisionTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws ScriptException {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
        String code = "function condition(a, b, c) {\n" +
                "    return ((a || b) && c);\n" +
                "}\n";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
    }

    @Test
    public void shouldCoverCondition() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        verify(new int[]{1, 0}, new int[]{1, 0}, new int[]{1, 0}, new int[]{0, 0}, new int[]{1, 0}, 1);
    }

    @Test
    public void shouldCoverAllPaths() throws ScriptException, NoSuchMethodException {
        verify(new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, 0);
        assertThat(invocable.invokeFunction("condition", false, false, true), is(false));
        verify(new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 0}, 1);
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        verify(new int[]{1, 1}, new int[]{1, 1}, new int[]{1, 1}, new int[]{0, 1}, new int[]{1, 0}, 2);
        assertThat(invocable.invokeFunction("condition", false, true, false), is(false));
        verify(new int[]{1, 2}, new int[]{2, 1}, new int[]{1, 2}, new int[]{1, 1}, new int[]{1, 1}, 3);
    }

    private String getConditionNumber(String json, int column, int length) {
        String regex = format("^.*\"(\\d+)\":\\{\"pos\":\\{\"line\":2,\"col\":%d,\"len\":%d}.*$", column, length);
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(json);
        if (m.matches())
            return m.group(1);
        throw new RuntimeException();
    }

    private void verify(int[] brABC, int[] brAB, int[] brA, int[] brB, int[] brC, int calls) throws ScriptException {
        /*
        "dM":{
        "1":{"pos":{"line":2,"col":12,"len":13}},//all
        "2":{"pos":{"line":2,"col":13,"len":6}}, //a||b
        "3":{"pos":{"line":2,"col":13,"len":1}}, //a
        "4":{"pos":{"line":2,"col":24,"len":1}}, //c
        "5":{"pos":{"line":2,"col":18,"len":1}}},//b
         */
        String dM = (String) engine.eval("JSON.stringify(jscover['test.js'].dM)");
        String pABC = getConditionNumber(dM, 12, 13);
        String pAB = getConditionNumber(dM, 13, 6);
        String pA = getConditionNumber(dM, 13, 1);
        String pB = getConditionNumber(dM, 18, 1);
        String pC = getConditionNumber(dM, 24, 1);

        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(calls));
        assertThat(engine.eval("jscover['test.js'].s['3']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(calls));
        assertThat(engine.eval("jscover['test.js'].dM['" + pABC + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].dM['" + pAB + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].dM['" + pA + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].dM['" + pB + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].dM['" + pC + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].d['" + pABC + "'][0]"), equalTo(brABC[0]));//Entire function
        assertThat(engine.eval("jscover['test.js'].d['" + pABC + "'][1]"), equalTo(brABC[1]));
        assertThat(engine.eval("jscover['test.js'].d['" + pAB + "'][0]"), equalTo(brAB[0]));//a||b
        assertThat(engine.eval("jscover['test.js'].d['" + pAB + "'][1]"), equalTo(brAB[1]));
        assertThat(engine.eval("jscover['test.js'].d['" + pA + "'][0]"), equalTo(brA[0]));//a
        assertThat(engine.eval("jscover['test.js'].d['" + pA + "'][1]"), equalTo(brA[1]));
        assertThat(engine.eval("jscover['test.js'].d['" + pC + "'][0]"), equalTo(brC[0]));//c
        assertThat(engine.eval("jscover['test.js'].d['" + pC + "'][1]"), equalTo(brC[1]));
        assertThat(engine.eval("jscover['test.js'].d['" + pB + "'][0]"), equalTo(brB[0]));//b
        assertThat(engine.eval("jscover['test.js'].d['" + pB + "'][1]"), equalTo(brB[1]));
    }
}
