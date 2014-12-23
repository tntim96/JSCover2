package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.jscomp.parsing.parser.LineNumberTable;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.StaticSourceFile;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;


public class Instrumenter {
    private static final Logger log = Logger.getLogger(Instrumenter.class.getName());
    private String booleanExpressionRecorderJS = "function(result, u, n) {if (result)this[u].be[''+n][0]++;else this[u].be[''+n][1]++;return result}";
    private String header;
    private Configuration config;

    //public Instrumenter() {
    //    this(new Configuration());
    //}

    public Instrumenter(Configuration config) {
        this.config = config;
        this.header = format("if (!%s) var %s = {beF: %s}\n", config.getCoverVariableName(), config.getCoverVariableName(), booleanExpressionRecorderJS);
    }

    public String instrument(String urlPath, String code) {
        SourceFile sourceFile = SourceFile.fromCode(urlPath, code);
        com.google.javascript.jscomp.parsing.parser.SourceFile sf = new com.google.javascript.jscomp.parsing.parser.SourceFile(urlPath, code);
        LineNumberTable lineNumberTable = new LineNumberTable(sf);
        Node jsRoot = parse(code, sourceFile);

        NodeWalker nodeWalker = new NodeWalker();
        NodeVisitorForStatements statementsVisitor = new NodeVisitorForStatements(config.getCoverVariableName(), sourceFile);
        NodeVisitorForFunctions functionVisitor = new NodeVisitorForFunctions(config.getCoverVariableName(), sourceFile);
        NodeVisitorForBooleanExpressions conditionVisitor = new NodeVisitorForBooleanExpressions(config.getCoverVariableName(), sourceFile, true);
        conditionVisitor = instrument(sourceFile, jsRoot, nodeWalker, statementsVisitor, functionVisitor, conditionVisitor);

        log.log(Level.FINEST, "{0}", jsRoot.toStringTree());
        CodePrinter.Builder builder = new CodePrinter.Builder(jsRoot);
        String header = buildHeader(urlPath, statementsVisitor, functionVisitor, conditionVisitor, lineNumberTable);
        String body = builder.build();
        return header + body;
    }

    private NodeVisitorForBooleanExpressions instrument(SourceFile sourceFile, Node jsRoot, NodeWalker nodeWalker, NodeVisitorForStatements statementsVisitor, NodeVisitorForFunctions functionVisitor, NodeVisitorForBooleanExpressions conditionVisitor) {
        if (config.isIncludeStatements()) {
            nodeWalker.visit(jsRoot, statementsVisitor);
        }
        if (config.isIncludeFunctions()) {
            nodeWalker.visit(jsRoot, functionVisitor);
        }
        if (config.isIncludeBranches()) {
            if (config.isIncludeBooleanExpressions()) {
                conditionVisitor = processConditions(sourceFile, jsRoot, nodeWalker);
            } else {
                nodeWalker.visit(jsRoot, conditionVisitor);
            }
        }
        return conditionVisitor;
    }

    private NodeVisitorForBooleanExpressions processConditions(SourceFile sourceFile, Node jsRoot, NodeWalker nodeWalker) {
        NodeVisitorForBooleanExpressions conditionVisitor = new NodeVisitorForBooleanExpressions(config.getCoverVariableName(), sourceFile, false);
        int parses = 0;
        while (++parses <= config.getMaxParses()) {
            log.log(Level.FINEST, "Condition parse number {0}", parses);
            int conditions = conditionVisitor.getBooleanExpressions().size();
            nodeWalker.visitAndExitOnAstChange(jsRoot, conditionVisitor);
            if (conditions == conditionVisitor.getBooleanExpressions().size()) {
                log.log(Level.FINE, "No AST condition changes after parse {0}", parses);
                break;
            }
        }
        if (parses > config.getMaxParses())
            log.log(Level.WARNING, "Stopping AST condition parsing after iteration {0}", parses-1);
        return conditionVisitor;
    }

    private String buildHeader(String urlPath, NodeVisitorForStatements statementsVisitor, NodeVisitorForFunctions functionVisitor, NodeVisitorForBooleanExpressions conditionVisitor, LineNumberTable lineNumberTable) {
        StringBuilder sb = new StringBuilder(header);
        sb.append(format("if (!%s['%s']) {\n", config.getCoverVariableName(), urlPath));
        sb.append(format("  %s['%s'] = {\n", config.getCoverVariableName(), urlPath));
        addStatements(sb, statementsVisitor, lineNumberTable);
        addBranches(sb, statementsVisitor, lineNumberTable);
        addBooleanExpressions(sb, conditionVisitor, lineNumberTable);
        addFunctions(sb, functionVisitor, lineNumberTable);
        sb.append("  }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void addStatements(StringBuilder sb, NodeVisitorForStatements nodeVisitor, LineNumberTable lineNumberTable) {
        sb.append("    \"s\":{");
        for (int i = 1; i <= nodeVisitor.getStatements().size(); i++) {
            if (i > 1)
                sb.append(",");
            sb.append(format("\"%d\":0", i));
        }
        sb.append("},\n");
        sb.append("    \"sM\":{");
        for (int i = 1; i <= nodeVisitor.getStatements().size(); i++) {
            Node n = nodeVisitor.getStatements().get(i - 1);
            if (i > 1)
                sb.append(",");
            int col = lineNumberTable.getColumn(n.getSourceOffset());
            sb.append(format("\"%d\":{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d}}", i, n.getLineno(), col, n.getLength()));
        }
        sb.append("},\n");
    }

    private void addBranches(StringBuilder sb, NodeVisitorForStatements nodeVisitor, LineNumberTable lineNumberTable) {
        sb.append("    \"b\":{");
        buildBranchProperty(sb, nodeVisitor);
        buildBranchMap(sb, nodeVisitor, lineNumberTable);
    }

    private void buildBranchMap(StringBuilder sb, NodeVisitorForStatements nodeVisitor, LineNumberTable lineNumberTable) {
        int i = 0;
        for (NodeWrapper parent : nodeVisitor.getBranches().keySet()) {
            if (++i > 1)
                sb.append(",");
            sb.append(format("\"%d\":[", parent.getId()));
            int j = 0;
            for (Node branch : nodeVisitor.getBranches().get(parent)) {
                if (++j > 1)
                    sb.append(",");
                int col = lineNumberTable.getColumn(branch.getSourceOffset());
                sb.append(format("{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d}}", branch.getLineno(), col, branch.getLength()));
            }
            sb.append("]");
        }
        sb.append("},\n");
    }

    private void buildBranchProperty(StringBuilder sb, NodeVisitorForStatements nodeVisitor) {
        int i = 0;
        for (NodeWrapper parent : nodeVisitor.getBranches().keySet()) {
            if (++i > 1)
                sb.append(",");
            sb.append(format("\"%d\":[", parent.getId()));
            for (int j = 1; j <= nodeVisitor.getBranches().get(parent).size(); j++) {
                if (j > 1)
                    sb.append(",");
                sb.append("0");
            }
            sb.append("]");
        }
        sb.append("},\n");
        sb.append("    \"bM\":{");
    }

    private void addBooleanExpressions(StringBuilder sb, NodeVisitorForBooleanExpressions nodeVisitor, LineNumberTable lineNumberTable) {
        sb.append("    \"be\":{");
        for (int i = 1; i <= nodeVisitor.getBooleanExpressions().size(); i++) {
            if (i > 1)
                sb.append(",");
            sb.append(format("\"%d\":[0,0]", i));
        }
        sb.append("},\n");
        sb.append("    \"beM\":{");
        for (int i = 1; i <= nodeVisitor.getBooleanExpressions().size(); i++) {
            BooleanExpression booleanExpression = nodeVisitor.getBooleanExpressions().get(i - 1);
            if (i > 1)
                sb.append(",");
            int col = lineNumberTable.getColumn(booleanExpression.getNode().getSourceOffset());
            sb.append(format("\"%d\":{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d},\"br\":\"%s\"}", i, booleanExpression.getNode().getLineno(), col, booleanExpression.getNode().getLength(), booleanExpression.isBranch()));
        }
        sb.append("},\n");
    }

    private void addFunctions(StringBuilder sb, NodeVisitorForFunctions nodeVisitor, LineNumberTable lineNumberTable) {
        sb.append("    \"f\":{");
        for (int i = 1; i <= nodeVisitor.getFunctions().size(); i++) {
            if (i > 1)
                sb.append(",");
            sb.append(format("\"%d\":0", i));
        }
        sb.append("},\n");
        sb.append("    \"fM\":{");
        for (int i = 1; i <= nodeVisitor.getFunctions().size(); i++) {
            Node n = nodeVisitor.getFunctions().get(i - 1);
            if (i > 1)
                sb.append(",");
            int col = lineNumberTable.getColumn(n.getSourceOffset());
            sb.append(format("\"%d\":{\"pos\":{\"line\":%d,\"col\":%d,\"len\":%d}}", i, n.getLineno(), col, n.getLength()));
        }
        sb.append("}\n");
    }

    private Node parse(String source, StaticSourceFile sourceFile) {
        Node script = ParserRunner.parse(
                sourceFile,
                source,
                ParserRunner.createConfig(true, config.getJavaScriptVersion(), false, null),
                null).ast;
        log.log(Level.FINEST, script.toStringTree());
        return script;
    }
}
