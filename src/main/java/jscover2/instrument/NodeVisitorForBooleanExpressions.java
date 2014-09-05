package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeVisitorForBooleanExpressions implements AstAlteredNodeCallback {
    private static final Logger log = Logger.getLogger(NodeVisitorForBooleanExpressions.class.getName());
    private NodeHelper nodeHelper = new NodeHelper();
    public List<BooleanExpression> branches = new ArrayList<>();
    private String coverVarName;
    private SourceFile sourceFile;
    private boolean excludeConditions;

    public NodeVisitorForBooleanExpressions(String coverVarName, SourceFile sourceFile, boolean excludeConditions) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
        this.excludeConditions = excludeConditions;
    }

    public List<BooleanExpression> getBooleanExpressions() {
        return branches;
    }

    @Override
    public boolean visit(Node n) {
        if (nodeHelper.isInstrumentation(n, coverVarName)) {
            return false;
        }
        log.log(Level.FINEST, "Visiting {0}", n);
        if (isBranch(n) &&
                !nodeHelper.isInstrumentation(n.getFirstChild(), coverVarName)) {
            addBranchRecorder(n);
            return true;
        } else if (!excludeConditions) {
            if (isBooleanJoin(n.getParent()) && !nodeHelper.isInstrumentation(n.getParent(), coverVarName)) {
                addConditionRecorder(n);
                return true;
            } else if (isBooleanTest(n) && !nodeHelper.isInstrumentation(n.getParent(), coverVarName)) {
                addConditionRecorder(n);
                return true;
            }
        }
        return false;
    }

    private boolean isBranch(Node n) {
        return n.isIf() || n.isHook();
    }

    private boolean isBooleanJoin(Node n) {
        if (n == null)
            return false;
        switch (n.getType()) {
            case Token.OR:
            case Token.AND:
                return true;
            default:
                return false;
        }
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
            case Token.OR:
            case Token.AND:
                return true;
            default:
                return false;
        }
    }

    private void addBranchRecorder(Node n) {
        log.log(Level.FINE, "----------------------------");
        log.log(Level.FINE, "Wrapping branch {0}", n);
        Node conditionNode = n.getFirstChild();
        branches.add(new BooleanExpression(conditionNode, true));
        log.log(Level.FINE, "Before {0}\n{1}", new Object[]{branches.size(), n.getFirstChild().toStringTree()});
        Node wrapper = nodeHelper.wrapConditionNode(conditionNode, coverVarName, sourceFile.getName(), branches.size());
        n.replaceChild(conditionNode, wrapper);
        log.log(Level.FINE, "After\n{0}", n.getFirstChild().toStringTree());
    }

    private void addConditionRecorder(Node n) {
        log.log(Level.FINE, "----------------------------");
        log.log(Level.FINE, "Wrapping condition {0}", n);
        branches.add(new BooleanExpression(n,false));
        Node parent = n.getParent();
        Node wrapper = nodeHelper.wrapConditionNode(n, coverVarName, sourceFile.getName(), branches.size());
        log.log(Level.FINE, "Before {0}\n{1}", new Object[]{branches.size(), parent.toStringTree()});
        parent.replaceChild(n, wrapper);
        log.log(Level.FINE, "After\n{0}", parent.toStringTree());
    }
}
