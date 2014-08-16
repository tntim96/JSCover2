package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.SimpleSourceFile;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ClosureCompilerTest {
    private Config.LanguageMode mode = Config.LanguageMode.ECMASCRIPT3;

    @Test
    public void shouldAddNode() {
        Node jsRoot = parse("x = 1;\ny=2;");
        jsRoot.addChildrenToBack(parse("z=3;"));
        CodePrinter.Builder builder = new CodePrinter.Builder(jsRoot);
        assertThat(builder.build(), equalTo("x=1;y=2;z=3"));
    }

    private Node parse(String source, String... warnings) {
        Node script = ParserRunner.parse(
                new SimpleSourceFile("input", false),
                source,
                ParserRunner.createConfig(true, false, mode, false, null),
                null).ast;
        return script;
    }
}
