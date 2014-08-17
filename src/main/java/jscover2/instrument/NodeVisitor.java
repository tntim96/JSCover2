package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeVisitor implements NodeCallback {
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> statements = new ArrayList<>();
    public List<Node> branches = new ArrayList<>();
    public List<Node> functions = new ArrayList<>();
    public List<Node> instrumentation = new ArrayList<>();
    private String coverVarName;
    private SourceFile sourceFile;

    public NodeVisitor(String coverVarName, SourceFile sourceFile) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
    }

    public List<Node> getStatements() {
        return statements;
    }

    public List<Node> getBranches() {
        return branches;
    }

    public List<Node> getFunctions() {
        return functions;
    }

    @Override
    public boolean shouldTraverse(Node n) {
        return !instrumentation.contains(n);
    }

    @Override
    public void visit(Node n) {
        if (isStatement(n))
            addStatementRecorder(n);
        if (n.isIf()) {
            addBranchStatementToIf(n);
        }
        if (n.isFunction())
            addFunctionRecorder(n);
        //System.out.println("n = " + n);
    }

    private boolean isStatement(Node n) {
        return n.isExprResult() || n.isVar() || n.isIf() || n.isReturn();
    }

    private void addStatementRecorder(Node node) {
        statements.add(node);
        Node instrumentNode = nodeHelper.createStatementIncrementNode(coverVarName, sourceFile.getName(), statements.size());
        instrumentation.add(instrumentNode);
        node.getParent().addChildBefore(instrumentNode, node);
    }

    private void addFunctionRecorder(Node node) {
        functions.add(node);
        Node instrumentNode = nodeHelper.createFunctionIncrementNode(coverVarName, sourceFile.getName(), functions.size());
        instrumentation.add(instrumentNode);
        node.getLastChild().addChildToFront(instrumentNode);
    }

    private void addBranchStatementToIf(Node node) {
        Node conditionNode = node.getFirstChild();
        branches.add(conditionNode);
        Node wrapper = nodeHelper.wrapConditionNode(conditionNode, "jscover", sourceFile.getName(), branches.size());
        instrumentation.add(wrapper);
        node.replaceChild(conditionNode, wrapper);
    }
}
