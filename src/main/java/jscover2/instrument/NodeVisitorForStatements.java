package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeVisitorForStatements implements NodeCallback {
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> statements = new ArrayList<>();
    public Map<NodeWrapper, List<Node>> branches = new HashMap<>();
    private String coverVarName;
    private SourceFile sourceFile;

    public NodeVisitorForStatements(String coverVarName, SourceFile sourceFile) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
    }

    public List<Node> getStatements() {
        return statements;
    }

    public Map<NodeWrapper, List<Node>> getBranches() {
        return branches;
    }

    @Override
    public void visit(Node n) {
        if (nodeHelper.isInstrumentation(n, coverVarName))
            return;
        if (isStatementToBeInstrumented(n))
            addStatementRecorder(n);
        else if (n.isCase() || n.isDefaultCase())
            addCaseBranchRecorder(n);
    }

    private void addCaseBranchRecorder(Node n) {
        Node switchNode = n.getParent();
        NodeWrapper nodeWrapper = new NodeWrapper(branches.size() + 1, switchNode);
        if (!branches.containsKey(nodeWrapper))
            branches.put(nodeWrapper, new ArrayList<>());
        branches.get(nodeWrapper).add(n);
        int branchNumber = branches.size();
        int pathArrayIndex = branches.get(nodeWrapper).size() - 1;
        Node branchRecordingNode = nodeHelper.createBranchIncrementNode(coverVarName, sourceFile.getName(), branchNumber, pathArrayIndex);
        Node block = n.getLastChild();
        block.addChildToFront(branchRecordingNode);
    }

    private boolean isStatementToBeInstrumented(Node n) {
        if (n.getParent() != null && !n.getParent().isBlock() && !n.getParent().isScript())
            return false;
        return n.isExprResult()
                || n.isFunction()
                || n.isVar()
                || n.isIf()
                || n.isDo()
                || n.isWhile()
                || n.isFor()
                || n.isBreak()
                || n.isSwitch()
                || n.isReturn();
    }

    private void addStatementRecorder(Node node) {
        statements.add(node);
        Node instrumentNode = nodeHelper.createStatementIncrementNode(coverVarName, sourceFile.getName(), statements.size());
        node.getParent().addChildBefore(instrumentNode, node);
    }
}
