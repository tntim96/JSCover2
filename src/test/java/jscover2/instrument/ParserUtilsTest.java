package jscover2.instrument;

import com.google.javascript.jscomp.parsing.parser.LineNumberTable;
import com.google.javascript.jscomp.parsing.parser.SourceFile;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class ParserUtilsTest {
    private ParserUtils parserUtils = ParserUtils.getInstance();
    private String code = "function sq(x) {\n" +
            "    return x * x;\n" +
            "}";
    private SourceFile sf = new SourceFile("test.js", code);
    private LineNumberTable lineNumberTable = new LineNumberTable(sf);

    @Test
    public void shouldGetLineOffset() {
        assertThat(parserUtils.getLineOffset(lineNumberTable, 1), equalTo(0));
        assertThat(parserUtils.getLineOffset(lineNumberTable, 2), equalTo(17));
        assertThat(parserUtils.getLineOffset(lineNumberTable, 3), equalTo(35));
        assertThat(parserUtils.getLineOffset(lineNumberTable, 4), equalTo(Integer.MAX_VALUE));
    }
}