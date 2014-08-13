package jscover2.instrument;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.Node;
import jscover2.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class NodeVisitor implements NodeCallback {
    private Config.LanguageMode mode = Config.LanguageMode.ECMASCRIPT3;
    public List<Node> statements = new ArrayList<>();
    public List<Pair<Node,Node>> branches = new ArrayList<>();
    public List<Node> instrumentation = new ArrayList<>();
    private SourceFile sourceFile;

    public NodeVisitor(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<Node> getStatements() {
        return statements;
    }

    public List<Pair<Node,Node>> getBranches() {
        return branches;
    }

    @Override
    public boolean shouldTraverse(Node n) {
        return !instrumentation.contains(n);
    }

    @Override
    public void visit(Node n) {
        if (isStatement(n))
            addStatement(n);
        if (n.isIf())
            addBranchStatementToIf(n);
        //System.out.println("n = " + n);
    }

    private void addBranchStatementToIf(Node n) {
        Node branch1 = parse(format("jscover['%s'].b['%d'][0]++;", sourceFile.getName(), branches.size()));
        Node branch2 = parse(format("jscover['%s'].b['%d'][1]++;", sourceFile.getName(), branches.size()));
        Pair<Node, Node> pair = new Pair<>(branch1, branch2);
        branches.add(pair);
        instrumentation.add(branch1);
        instrumentation.add(branch2);
        n.getLastChild().addChildToFront(branch1);
    }

    private boolean isStatement(Node n) {
        return n.isExprResult() || n.isVar() || n.isIf();
    }

    private void addStatement(Node node) {
        statements.add(node);
        Node instrumentNode = parse(format("jscover['%s'].s['%d']++;", sourceFile.getName(), statements.size()));
        instrumentation.add(instrumentNode);
        node.getParent().addChildBefore(instrumentNode, node);
    }


    private Node parse(String source, String... warnings) {
        Node script = ParserRunner.parse(
                sourceFile,
                source,
                ParserRunner.createConfig(true, mode, false),
                null).ast;
        return script;
    }

}
