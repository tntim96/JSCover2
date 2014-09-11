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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
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
    public void shouldCalculateStatementCoverage() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x)\n{\n  return x * x;\n}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        FileData fileData = new FileData(json);
        assertThat(fileData.getStatements().size(), is(2));
        CoverageData firstStatement = fileData.getStatements().get(0);
        assertThat(firstStatement.getHits(), is(1));
        assertThat(firstStatement.getPosition().getLine(), is(1));
        assertThat(firstStatement.getPosition().getColumn(), is(0));
        assertThat(firstStatement.getPosition().getLength(), is(34));
        assertThat(fileData.getStatements().get(1).getHits(), is(0));
        assertThat(fileData.getStatements().get(1).getPosition().getLine(), is(3));
        assertThat(fileData.getStatements().get(1).getPosition().getColumn(), is(2));
        assertThat(fileData.getStatements().get(1).getPosition().getLength(), is(13));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getStatements().get(0).getHits(), is(1));
        assertThat(fileData.getStatements().get(1).getHits(), is(1));
    }

    @Test
    public void shouldCalculateAddStatementsToLineData() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x)\n{\n  return x * x;\n}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        FileData fileData = new FileData(json);
        assertThat(fileData.getLineData().get(0), nullValue());
        assertThat(fileData.getLineData().get(1).getStatements().size(), is(1));
        assertThat(fileData.getLineData().get(2), nullValue());
        assertThat(fileData.getLineData().get(3).getStatements().size(), is(1));
    }

    @Test
    public void shouldCalculateStatementAndLineCoverageOnSingleLine() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x){return x * x;}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        FileData fileData = new FileData(json);

        assertThat(fileData.getStatements().size(), is(2));
        assertThat(fileData.getStatements().get(0).getHits(), is(1));
        assertThat(fileData.getStatements().get(0).getPosition().getLine(), is(1));
        assertThat(fileData.getStatements().get(0).getPosition().getColumn(), is(0));
        assertThat(fileData.getStatements().get(0).getPosition().getLength(), is(29));
        assertThat(fileData.getStatements().get(1).getHits(), is(0));
        assertThat(fileData.getStatements().get(1).getPosition().getLine(), is(1));
        assertThat(fileData.getStatements().get(1).getPosition().getColumn(), is(15));
        assertThat(fileData.getStatements().get(1).getPosition().getLength(), is(13));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getStatements().get(0).getHits(), is(1));
        assertThat(fileData.getStatements().get(1).getHits(), is(1));
    }

    @Test
    public void shouldCalculateFunctionCoverage() throws Exception {
        String instrumented = instrumenter.instrument("test.js", "function sq(x){return x*x;}\n" +
                "function cube(x){return x*x*x;}");
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        FileData fileData = new FileData(json);
        assertThat(fileData.getFunctions().get(0).getHits(), is(0));
        assertThat(fileData.getFunctions().get(0).getPosition().getLine(), is(1));
        assertThat(fileData.getFunctions().get(0).getPosition().getColumn(), is(0));
        assertThat(fileData.getFunctions().get(0).getPosition().getLength(), is(27));
        assertThat(fileData.getFunctions().get(1).getHits(), is(0));
        assertThat(fileData.getFunctions().get(1).getPosition().getLine(), is(2));
        assertThat(fileData.getFunctions().get(1).getPosition().getColumn(), is(0));
        assertThat(fileData.getFunctions().get(1).getPosition().getLength(), is(31));

        assertThat(invocable.invokeFunction("sq", 5), is(25.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getFunctions().get(0).getHits(), is(1));
        assertThat(fileData.getFunctions().get(1).getHits(), is(0));

        assertThat(invocable.invokeFunction("cube", 5), is(125.0));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getFunctions().get(0).getHits(), is(1));
        assertThat(fileData.getFunctions().get(1).getHits(), is(1));
    }

    @Test
    public void shouldCalculateBooleanExpressionAndBranchCoverage() throws Exception {
        String code = "function condition(a, b, c) {\n" +
                "    if ((a || b) && c)\n" +
                "        return true;\n" +
                "    else;\n" +
                "        return false;\n" +
                "}";
        SourceCodeRetriever codeRetriever = new SourceCodeRetriever("test.js", code);
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        FileData fileData = new FileData(json);

        /*
        "beM":{
        "1":{"pos":{"line":2,"col":8,"len":13},"br":"true"},//abc
        "2":{"pos":{"line":2,"col":9,"len":6},"br":"false"},//a||b
        "3":{"pos":{"line":2,"col":9,"len":1},"br":"false"},//a
        "4":{"pos":{"line":2,"col":20,"len":1},"br":"false"},//c
        "5":{"pos":{"line":2,"col":14,"len":1},"br":"false"}},//b      
         */

        String beM = (String) engine.eval("JSON.stringify(jscover['test.js'].beM)");
        int pABC = getConditionNumber(beM, 8, 13);
        int pAB = getConditionNumber(beM, 9, 6);
        int pA = getConditionNumber(beM, 9, 1);
        int pB = getConditionNumber(beM, 14, 1);
        int pC = getConditionNumber(beM, 20, 1);

        assertThat(fileData.getLineData().get(0), nullValue());
        assertThat(fileData.getLineData().get(1).getBooleanExpressions().size(), is(0));
        assertThat(fileData.getLineData().get(2).getBooleanExpressions().size(), is(5));
        assertThat(fileData.getLineData().get(2).isBooleanMissed(), is(true));
        assertThat(fileData.getLineData().get(3).getBooleanExpressions().size(), is(0));
        assertThat(fileData.getLineData().get(4), nullValue());
        assertThat(fileData.getLineData().get(5).getBooleanExpressions().size(), is(0));
        assertThat(fileData.getLineData().get(6), nullValue());

        assertThat(fileData.getBooleanBranches().size(), is(1));
        assertThat(fileData.getBooleanBranches().iterator().next(), is(fileData.getBooleanExpressions().get(pABC)));
        assertThat(fileData.getBooleanExpressions().size(), is(5));
        assertThat(fileData.getBooleanExpressions().get(pABC).isBranch(), is(true));
        assertThat(fileData.getBooleanExpressions().get(pABC).getPosition().getLine(), is(2));
        assertThat(fileData.getBooleanExpressions().get(pABC).getPosition().getColumn(), is(8));
        assertThat(fileData.getBooleanExpressions().get(pABC).getPosition().getLength(), is(13));
        assertThat(codeRetriever.getSource(fileData.getBooleanExpressions().get(pABC).getPosition()), equalTo("(a || b) && c"));
        assertThat(fileData.getBooleanExpressions().get(pABC).getFalseHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pABC).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pAB).getFalseHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pAB).getTrueHits(), is(0));
        assertThat(codeRetriever.getSource(fileData.getBooleanExpressions().get(pAB).getPosition()), equalTo("a || b"));
        assertThat(fileData.getBooleanExpressions().get(pA).getFalseHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pA).getTrueHits(), is(0));
        assertThat(codeRetriever.getSource(fileData.getBooleanExpressions().get(pA).getPosition()), equalTo("a"));
        assertThat(fileData.getBooleanExpressions().get(pB).getFalseHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pB).getTrueHits(), is(0));
        assertThat(codeRetriever.getSource(fileData.getBooleanExpressions().get(pB).getPosition()), equalTo("b"));
        assertThat(fileData.getBooleanExpressions().get(pC).getFalseHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pC).getTrueHits(), is(0));
        assertThat(codeRetriever.getSource(fileData.getBooleanExpressions().get(pC).getPosition()), equalTo("c"));

        assertThat(invocable.invokeFunction("condition", false, false, true), is(false));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getBooleanExpressions().get(pABC).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pABC).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pAB).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pAB).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pA).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pA).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pB).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pB).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pC).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pC).getFalseHits(), is(0));
        assertThat(fileData.getLineData().get(2).isBooleanMissed(), is(true));

        assertThat(invocable.invokeFunction("condition", true, false, true), is(true));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getBooleanExpressions().get(pABC).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pABC).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pAB).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pAB).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pA).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pA).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pB).getTrueHits(), is(0));
        assertThat(fileData.getBooleanExpressions().get(pB).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pC).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pC).getFalseHits(), is(0));
        assertThat(fileData.getLineData().get(2).isBooleanMissed(), is(true));

        assertThat(invocable.invokeFunction("condition", false, true, false), is(false));
        json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        fileData = new FileData(json);
        assertThat(fileData.getBooleanExpressions().get(pABC).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pABC).getFalseHits(), is(2));
        assertThat(fileData.getBooleanExpressions().get(pAB).getTrueHits(), is(2));
        assertThat(fileData.getBooleanExpressions().get(pAB).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pA).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pA).getFalseHits(), is(2));
        assertThat(fileData.getBooleanExpressions().get(pB).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pB).getFalseHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pC).getTrueHits(), is(1));
        assertThat(fileData.getBooleanExpressions().get(pC).getFalseHits(), is(1));
        assertThat(fileData.getLineData().get(2).isBooleanMissed(), is(false));
    }

    @Test
    public void shouldCalculateBranchCoverage() throws Exception {
        String code = "function sw1(x) {\nvar y; switch(x) {\ncase 1:\ncase 2:\ncase 3: return y = 'three'; break;\ncase 4: \nreturn y;\n}\n}\n"
                + "function sw2(x) {\nvar y; switch(x) {default: y = 10}\nreturn y;\n}";
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
        ScriptObjectMirror json = (ScriptObjectMirror) engine.eval("jscover['test.js']");
        FileData fileData = new FileData(json);

        assertThat(fileData.getLineData().get(0), nullValue());
        assertThat(fileData.getLineData().get(1).getBranchPaths().size(), is(0));
        assertThat(fileData.getLineData().get(2).getBranchPaths().size(), is(0));
        assertThat(fileData.getLineData().get(3).getBranchPaths().size(), is(1));
        assertThat(fileData.getLineData().get(4).getBranchPaths().size(), is(1));
        assertThat(fileData.getLineData().get(5).getBranchPaths().size(), is(1));
        assertThat(fileData.getLineData().get(6).getBranchPaths().size(), is(1));
        assertThat(fileData.getLineData().get(7).getBranchPaths().size(), is(0));
        assertThat(fileData.getLineData().get(8), nullValue());

        assertThat(fileData.getBranchPaths().size(), is(5));
        assertThat(fileData.getBranchPaths().get(0).getHits(), is(0));
        assertThat(fileData.getBranchPaths().get(1).getHits(), is(0));
        assertThat(fileData.getBranchPaths().get(2).getHits(), is(0));
        assertThat(fileData.getBranchPaths().get(3).getHits(), is(0));
        assertThat(fileData.getBranchPaths().get(4).getHits(), is(0));

        assertThat(invocable.invokeFunction("sw1", 2), equalTo("three"));
        fileData = new FileData(json);
        for (CoverageData bp : fileData.getBranchPaths()) {
            if (bp.getPosition().getLine() == 3) assertThat(bp.getHits(), is(0));
            if (bp.getPosition().getLine() == 4) assertThat(bp.getHits(), is(1));
            if (bp.getPosition().getLine() == 5) assertThat(bp.getHits(), is(1));
            if (bp.getPosition().getLine() == 6) assertThat(bp.getHits(), is(0));
            if (bp.getPosition().getLine() == 11) assertThat(bp.getHits(), is(0));
        }

        assertThat(invocable.invokeFunction("sw2", 2), equalTo(10));
        fileData = new FileData(json);
        assertThat(fileData.getBranchPaths().get(4).getHits(), is(1));
        for (CoverageData bp : fileData.getBranchPaths()) {
            if (bp.getPosition().getLine() == 3) assertThat(bp.getHits(), is(0));
            if (bp.getPosition().getLine() == 4) assertThat(bp.getHits(), is(1));
            if (bp.getPosition().getLine() == 5) assertThat(bp.getHits(), is(1));
            if (bp.getPosition().getLine() == 6) assertThat(bp.getHits(), is(0));
            if (bp.getPosition().getLine() == 11) assertThat(bp.getHits(), is(1));
        }
    }

    private int getConditionNumber(String json, int column, int length) {
        String regex = format("^.*\"(\\d+)\":\\{\"pos\":\\{\"line\":2,\"col\":%d,\"len\":%d}.*$", column, length);
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(json);
        if (m.matches())
            return new Integer(m.group(1))-1;
        throw new RuntimeException();
    }
}
