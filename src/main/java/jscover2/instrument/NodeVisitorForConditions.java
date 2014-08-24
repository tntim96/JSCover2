package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeVisitorForConditions implements NodeCallback {
    private static final Logger log = Logger.getLogger(NodeVisitorForConditions.class.getName());
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> branches = new ArrayList<>();
    private String coverVarName;
    private SourceFile sourceFile;

    public NodeVisitorForConditions(String coverVarName, SourceFile sourceFile) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
    }

    public List<Node> getBranches() {
        return branches;
    }

    @Override
    public void visit(Node n) {
        if (isInstrumentation(n)) {
            return;
        }
        log.log(Level.FINEST, "Visiting {0}", n);
        if (isBranch(n)) {
            addBranchRecorder(n);
        } else if (isBooleanTest(n) && !isInstrumentation(n.getParent())) {
            addConditionRecorder(n);
        } else if (isBooleanJoin(n)) {
            if (!isInstrumentation(n.getFirstChild()))
                addConditionRecorder(n.getFirstChild());
            if (!isInstrumentation(n.getLastChild()))
                addConditionRecorder(n.getLastChild());
        }
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
        if (n == null)
            return false;
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
        Node parent = n.getParent();
        Node wrapper = nodeHelper.wrapConditionNode(n, coverVarName, sourceFile.getName(), branches.size());
        log.log(Level.FINEST, "Before\n{0}", parent.toStringTree());
        parent.replaceChild(n, wrapper);
        log.log(Level.FINEST, "After\n{0}", parent.toStringTree());
    }
}
