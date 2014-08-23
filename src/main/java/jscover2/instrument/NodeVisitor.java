package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

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
        if (isInstrumentation(n)) {
            return;
        }
        log.log(Level.FINEST, "Visiting {0}", n);
        if (isStatementToBeInstrumented(n))
            addStatementRecorder(n);
        if (isBranch(n)) {
            addBranchRecorder(n);
        } else if (isBooleanTest(n) && !isInstrumentation(n.getParent())) {
            addConditionRecorder(n);
        } else if (isBooleanJoin(n)) {
            addConditionRecorder(n.getFirstChild());
            addConditionRecorder(n.getLastChild());
        }
        if (n.isFunction())
            addFunctionRecorder(n);
        //System.out.println("n = " + n);
    }

    private boolean isBranch(Node n) {
        return n.isIf() || n.isHook();
    }

    private boolean isBooleanJoin(Node n) {
        switch (n.getType()) {
            case Token.OR:
            case Token.AND:
                return true;
        }
        return false;
    }

    private boolean isBooleanTest(Node n) {
        switch (n.getType()) {
            case Token.EQ:
            case Token.NE:
            case Token.LT:
            case Token.LE:
            case Token.GT:
            case Token.GE:
            case Token.SHEQ:
            case Token.SHNE:
                return true;
        }
        return false;
    }

    private boolean isInstrumentation(Node n) {
        if (n.getSourceFileName() == null)
            return true;
        Node child = n.getFirstChild();
        if (child !=null && child.isGetProp() && child.getFirstChild().getString().equals(coverVarName))
            return true;
        return false;
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

    private void addBranchRecorder(Node node) {
        Node conditionNode = node.getFirstChild();
        branches.add(conditionNode);
        Node wrapper = nodeHelper.wrapConditionNode(conditionNode, coverVarName, sourceFile.getName(), branches.size());
        node.replaceChild(conditionNode, wrapper);
    }

    private void addConditionRecorder(Node n) {
        log.log(Level.FINEST, "----------------------------");
        log.log(Level.FINEST, "Wrapping {0}", n);
        branches.add(n);
        Node wrapper = nodeHelper.wrapConditionNode(n, coverVarName, sourceFile.getName(), branches.size());
        Node parent = n.getParent();
        log.log(Level.FINEST, "Before\n{0}", parent.toStringTree());
        parent.replaceChild(n, wrapper);
        log.log(Level.FINEST, "After\n{0}", parent.toStringTree());
    }
}
