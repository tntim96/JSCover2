package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.jscomp.parsing.parser.LineNumberTable;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.StaticSourceFile;

import static java.lang.String.format;


public class Instrumenter {
    private String branchRecorderJS = "function(result, u, n) {if (result)this[u].b[''+n][0]++;else this[u].b[''+n][1]++;return result}";
    private String header = String.format("if (!jscover) var jscover = {bF: %s};\n",branchRecorderJS);
    private Config.LanguageMode mode = Config.LanguageMode.ECMASCRIPT3;

    public String instrument(String urlPath, String code) {
        SourceFile sourceFile = SourceFile.fromCode(urlPath, urlPath, code);
        NodeVisitor nodeVisitor = new NodeVisitor(sourceFile);
        com.google.javascript.jscomp.parsing.parser.SourceFile sf = new com.google.javascript.jscomp.parsing.parser.SourceFile(urlPath, code);
        LineNumberTable lineNumberTable = new LineNumberTable(sf);
        Node jsRoot = parse(code, sourceFile);
        new NodeWalker().visit(jsRoot, nodeVisitor);
        CodePrinter.Builder builder = new CodePrinter.Builder(jsRoot);
        String header = buildHeader(urlPath, nodeVisitor, lineNumberTable);
        String body = builder.build();
        return header + body;
    }

    private String buildHeader(String urlPath, NodeVisitor nodeVisitor, LineNumberTable lineNumberTable) {
        StringBuilder sb = new StringBuilder(header);
        sb.append(format("if (!jscover['%s']) {\n", urlPath));
        sb.append(format("  jscover['%s'] = {\n", urlPath));
        addStatements(sb, nodeVisitor, lineNumberTable);
        addBranches(sb, nodeVisitor, lineNumberTable);
        addFunctions(sb, nodeVisitor, lineNumberTable);
        sb.append("  }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void addStatements(StringBuilder sb, NodeVisitor nodeVisitor, LineNumberTable lineNumberTable) {
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
        sb.append("},\n");
    }

    private void addBranches(StringBuilder sb, NodeVisitor nodeVisitor, LineNumberTable lineNumberTable) {
        sb.append("    \"b\":{");
        for (int i = 1; i <= nodeVisitor.getBranches().size(); i++) {
            if (i > 1)
                sb.append(",");
            sb.append(format("\"%d\":[0,0]", i));
        }
        sb.append("},\n");
        sb.append("    \"bD\":{");
        for (int i = 1; i <= nodeVisitor.getBranches().size(); i++) {
            Node n = nodeVisitor.getBranches().get(i-1);
            if (i > 1)
                sb.append(",");
            int col = lineNumberTable.getColumn(n.getSourceOffset());
            sb.append(format("\"%d\":{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d}}", i, n.getLineno(), col, n.getLength()));
        }
        sb.append("},\n");
    }

    private void addFunctions(StringBuilder sb, NodeVisitor nodeVisitor, LineNumberTable lineNumberTable) {
        sb.append("    \"f\":{");
        for (int i = 1; i <= nodeVisitor.getStatements().size(); i++) {
            if (i > 1)
                sb.append(",");
            sb.append(format("\"%d\":0", i));
        }
        sb.append("},\n");
        sb.append("    \"fD\":{");
        for (int i = 1; i <= nodeVisitor.getStatements().size(); i++) {
            Node n = nodeVisitor.getStatements().get(i-1);
            if (i > 1)
                sb.append(",");
            int col = lineNumberTable.getColumn(n.getSourceOffset());
            sb.append(format("\"%d\":{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d}}", i, n.getLineno(), col, n.getLength()));
        }
        sb.append("},\n");
    }

    private Node parse(String source, StaticSourceFile sourceFile) {
        Node script = ParserRunner.parse(
                sourceFile,
                source,
                ParserRunner.createConfig(true, false, mode, false, null),
                null).ast;
        //System.out.println(script.toStringTree());
        return script;
    }
}
