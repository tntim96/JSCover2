package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.SimpleSourceFile;

public class Instrumenter {
    private String header = "if (!jscover) var jscover = {};\n";
    private Config.LanguageMode mode = Config.LanguageMode.ECMASCRIPT3;
    private NodeVisitor nodeVisitor = new NodeVisitor();

    public String instrument(String js) {
        Node jsRoot = parse(js);
        Compiler compiler = new Compiler();
        NodeTraversal.traverse(compiler, jsRoot, nodeVisitor);
        CodePrinter.Builder builder = new CodePrinter.Builder(jsRoot);
        String header = buildHeader();
        String body = builder.build();
        return header + body;
    }

    private String buildHeader() {
        StringBuilder sb = new StringBuilder(header);
        sb.append("if (!jscover['test.js']) {\njscover['test.js'] = {};\njscover['test.js'].s = [];\n");
        for (Node node : nodeVisitor.getStatements()) {
            sb.append(String.format("  jscover['test.js'].s[%d] = 0;\n", node.getLineno()));
        }
        sb.append("}\n");
        sb.append("if (! jscover['test.js'].f) {\n" +
                "  jscover['test.js'].f = [];\n" +
                "}\n" +
                "if (!jscover['test.js'].b) {\n" +
                "  jscover['test.js'].b = {};\n" +
                "}\n");
        return sb.toString();
    }

    private Node parse(String source, String... warnings) {
        Node script = ParserRunner.parse(
                new SimpleSourceFile("input", false),
                source,
                ParserRunner.createConfig(true, mode, false),
                null).ast;
        return script;
    }
}
