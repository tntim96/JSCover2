package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeVisitor implements NodeCallback {
    private static final Logger log = Logger.getLogger(NodeVisitor.class.getName());
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> statements = new ArrayList<>();
    public List<Node> branches = new ArrayList<>();
    public List<Node> functions = new ArrayList<>();
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
    public void visit(Node n) {
        if (isInstrumented(n)) {
            return;
        }
        log.log(Level.FINEST, "Visiting {0}", n);
        if (isStatementToBeInstrumented(n))
            addStatementRecorder(n);
        if (n.isIf() || n.isHook()) {
            addConditionRecorder(n);
        }
        if (n.isFunction())
            addFunctionRecorder(n);
        //System.out.println("n = " + n);
    }

    private boolean isInstrumented(Node n) {
        return n.getSourceFileName() == null;
    }

    private boolean isStatementToBeInstrumented(Node n) {
        if (n.getParent() != null && !n.getParent().isBlock() && !n.getParent().isScript())
            return false;
        return n.isExprResult()
                || n.isVar()
                || n.isIf()
                || n.isDo()
                || n.isWhile()
                || n.isFor()
                || n.isForOf()
                || n.isReturn();
    }

    private void addFunctionRecorder(Node node) {
        functions.add(node);
        Node instrumentNode = nodeHelper.createFunctionIncrementNode(coverVarName, sourceFile.getName(), functions.size());
        node.getLastChild().addChildToFront(instrumentNode);
    }

    private void addStatementRecorder(Node node) {
        statements.add(node);
        Node instrumentNode = nodeHelper.createStatementIncrementNode(coverVarName, sourceFile.getName(), statements.size());
        node.getParent().addChildBefore(instrumentNode, node);
    }

    private void addConditionRecorder(Node node) {
        Node conditionNode = node.getFirstChild();
        branches.add(conditionNode);
        Node wrapper = nodeHelper.wrapConditionNode(conditionNode, coverVarName, sourceFile.getName(), branches.size());
        node.replaceChild(conditionNode, wrapper);
    }
}
