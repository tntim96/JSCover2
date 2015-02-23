package jscover2.instrument;

import com.google.javascript.jscomp.parsing.parser.LineNumberTable;

public class ParserUtils {
    private static ParserUtils parserUtils = new ParserUtils();

    static ParserUtils getInstance() {
        return parserUtils;
    }


    int getLineOffset(LineNumberTable lineNumberTable, int lineNo) {
        return lineNumberTable.offsetOfLine(lineNo - 1);
    }

}
