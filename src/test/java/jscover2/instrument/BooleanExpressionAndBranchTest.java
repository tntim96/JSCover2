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

public class BooleanExpressionAndBranchTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws ScriptException {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
        String code = "function condition(a, b, c) {\n" +
                "    if ((a || b) && c)\n" +
                "        return true;\n" +
                "    else;\n" +
                "        return false;\n" +
                "}\n";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
    }

    @Test
    public void shouldCoverCondition() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        verify(new int[]{1, 0}, new int[]{1, 0}, new int[]{1, 0}, new int[]{0, 0}, new int[]{1, 0}, 1, 1, 0);
    }

    @Test
    public void shouldCoverAllPaths() throws ScriptException, NoSuchMethodException {
        verify(new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, 0, 0, 0);
        assertThat(invocable.invokeFunction("condition", false, false, true), is(false));
        verify(new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 0}, 1, 0, 1);
        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        verify(new int[]{1, 1}, new int[]{1, 1}, new int[]{1, 1}, new int[]{0, 1}, new int[]{1, 0}, 2, 1, 1);
        assertThat(invocable.invokeFunction("condition", false, true, false), is(false));
        verify(new int[]{1, 2}, new int[]{2, 1}, new int[]{1, 2}, new int[]{1, 1}, new int[]{1, 1}, 3, 1, 2);
    }

    private String getConditionNumber(String json, int column, int length) {
        String regex = format("^.*\"(\\d+)\":\\{\"pos\":\\{\"line\":2,\"col\":%d,\"len\":%d}.*$", column, length);
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(json);
        if (m.matches())
            return m.group(1);
        throw new RuntimeException();
    }

    private void verify(int[] brABC, int[] brAB, int[] brA, int[] brB, int[] brC, int calls, int s3, int s4) throws ScriptException {
        /*
        "beM":{
        "1":{"pos":{"line":2,"col":8,"len":13},"br":"true"},    //all
        "2":{"pos":{"line":2,"col":9,"len":6},"br":"false"},    //a||b
        "3":{"pos":{"line":2,"col":9,"len":1},"br":"false"},    //a
        "4":{"pos":{"line":2,"col":20,"len":1},"br":"false"},   //c
        "5":{"pos":{"line":2,"col":14,"len":1},"br":"false"}},  //b
         */
        String beM = (String) engine.eval("JSON.stringify(jscover['test.js'].beM)");
        String pABC = getConditionNumber(beM, 8, 13);
        String pAB = getConditionNumber(beM, 9, 6);
        String pA = getConditionNumber(beM, 9, 1);
        String pB = getConditionNumber(beM, 14, 1);
        String pC = getConditionNumber(beM, 20, 1);

        assertThat(engine.eval("jscover['test.js'].s['1']"), equalTo(1));
        assertThat(engine.eval("jscover['test.js'].s['2']"), equalTo(calls));
        assertThat(engine.eval("jscover['test.js'].s['3']"), equalTo(s3));
        assertThat(engine.eval("jscover['test.js'].s['4']"), equalTo(s4));
        assertThat(engine.eval("jscover['test.js'].s['5']"), nullValue());
        assertThat(engine.eval("jscover['test.js'].f['1']"), equalTo(calls));
        assertThat(engine.eval("jscover['test.js'].beM['" + pABC + "'].br"), equalTo("true"));
        assertThat(engine.eval("jscover['test.js'].beM['" + pAB + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].beM['" + pA + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].beM['" + pB + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].beM['" + pC + "'].br"), equalTo("false"));
        assertThat(engine.eval("jscover['test.js'].be['" + pABC + "'][0]"), equalTo(brABC[0]));//Entire function
        assertThat(engine.eval("jscover['test.js'].be['" + pABC + "'][1]"), equalTo(brABC[1]));
        assertThat(engine.eval("jscover['test.js'].be['" + pAB + "'][0]"), equalTo(brAB[0]));//a||b
        assertThat(engine.eval("jscover['test.js'].be['" + pAB + "'][1]"), equalTo(brAB[1]));
        assertThat(engine.eval("jscover['test.js'].be['" + pA + "'][0]"), equalTo(brA[0]));//a
        assertThat(engine.eval("jscover['test.js'].be['" + pA + "'][1]"), equalTo(brA[1]));
        assertThat(engine.eval("jscover['test.js'].be['" + pC + "'][0]"), equalTo(brC[0]));//c
        assertThat(engine.eval("jscover['test.js'].be['" + pC + "'][1]"), equalTo(brC[1]));
        assertThat(engine.eval("jscover['test.js'].be['" + pB + "'][0]"), equalTo(brB[0]));//b
        assertThat(engine.eval("jscover['test.js'].be['" + pB + "'][1]"), equalTo(brB[1]));
    }
}
