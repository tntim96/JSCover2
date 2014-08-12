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
        sb.append("if (!jscover['test.js']) {\n");
        sb.append("  jscover['test.js'] = {\n");
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
            sb.append(String.format("\"%d\":0", i));
        }
        sb.append("}\n");
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
