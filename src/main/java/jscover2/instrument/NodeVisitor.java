package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeVisitor implements NodeCallback {
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> statements = new ArrayList<>();
    public List<Node> branches = new ArrayList<>();
    public List<Node> instrumentation = new ArrayList<>();
    private SourceFile sourceFile;

    public NodeVisitor(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<Node> getStatements() {
        return statements;
    }

    public List<Node> getBranches() {
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

    private boolean isStatement(Node n) {
        return n.isExprResult() || n.isVar() || n.isIf();
    }

    private void addStatement(Node node) {
        statements.add(node);
        Node instrumentNode = nodeHelper.createStatementIncrementNode("jscover", sourceFile.getName(), statements.size());
        instrumentation.add(instrumentNode);
        node.getParent().addChildBefore(instrumentNode, node);
    }

    private void addBranchStatementToIf(Node n) {
        Node conditionNode = n.getFirstChild();
        branches.add(conditionNode);
        Node wrapper = nodeHelper.wrapConditionNode(conditionNode, "jscover", sourceFile.getName(), branches.size());
        instrumentation.add(wrapper);
        n.replaceChild(conditionNode, wrapper);
    }
}
