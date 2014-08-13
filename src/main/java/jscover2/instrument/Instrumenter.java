package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.jscomp.parsing.parser.LineNumberTable;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.StaticSourceFile;

import static java.lang.String.format;


public class Instrumenter {
    private String header = "if (!jscover) var jscover = {};\n";
    private Config.LanguageMode mode = Config.LanguageMode.ECMASCRIPT3;
    private NodeVisitor nodeVisitor = new NodeVisitor();
    private LineNumberTable lineNumberTable;

    public String instrument(String urlPath, String code) {
        SourceFile sourceFile = SourceFile.fromCode(urlPath, urlPath, code);
        com.google.javascript.jscomp.parsing.parser.SourceFile sf = new com.google.javascript.jscomp.parsing.parser.SourceFile(urlPath, code);
        lineNumberTable = new LineNumberTable(sf);
        Node jsRoot = parse(code, sourceFile);
        Compiler compiler = new Compiler();
        NodeTraversal.traverse(compiler, jsRoot, nodeVisitor);
        CodePrinter.Builder builder = new CodePrinter.Builder(jsRoot);
        String header = buildHeader(urlPath);
        String body = builder.build();
        return header + body;
    }

    private String buildHeader(String urlPath) {
        StringBuilder sb = new StringBuilder(header);
        sb.append(format("if (!jscover['%s']) {\n", urlPath));
        sb.append(format("  jscover['%s'] = {\n", urlPath));
        addStatements(sb);
        sb.append("  };\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void addStatements(StringBuilder sb) {
        sb.append("    \"s\":{");
        for (int i = 1; i <= nodeVisitor.getStatements().size(); i++) {
            if (i > 1)
                sb.append(",");
            sb.append(format("\"%d\":0", i));
        }
        sb.append("},\n");
        sb.append("    \"sD\":{");
        for (int i = 1; i <= nodeVisitor.getStatements().size(); i++) {
            Node n = nodeVisitor.getStatements().get(i-1);
            if (i > 1)
                sb.append(",");
            int col = lineNumberTable.getColumn(n.getSourceOffset());
            sb.append(format("\"%d\":{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d}}", i, n.getLineno(), col, n.getLength()));
        }
        sb.append("}\n");
    }

    private Node parse(String source, StaticSourceFile sourceFile, String... warnings) {
        Node script = ParserRunner.parse(
                sourceFile,
                source,
                ParserRunner.createConfig(true, mode, false),
                null).ast;
        return script;
    }
}
