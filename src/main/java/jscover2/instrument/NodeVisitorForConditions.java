package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeVisitorForConditions implements AstAlteredNodeCallback {
    private static final Logger log = Logger.getLogger(NodeVisitorForConditions.class.getName());
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Condition> branches = new ArrayList<>();
    private String coverVarName;
    private SourceFile sourceFile;
    private boolean excludeConditions;

    public NodeVisitorForConditions(String coverVarName, SourceFile sourceFile, boolean excludeConditions) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
        this.excludeConditions = excludeConditions;
    }

    public List<Condition> getBranches() {
        return branches;
    }

    @Override
    public boolean visit(Node n) {
        if (isInstrumentation(n)) {
            return false;
        }
        log.log(Level.FINEST, "Visiting {0}", n);
        if (isBranch(n) && !isInstrumentation(n.getFirstChild())) {
            addBranchRecorder(n);
            return true;
        } else if (!excludeConditions) {
            if (isBooleanJoin(n.getParent()) && !isInstrumentation(n.getParent())) {
                addConditionRecorder(n);
                return true;
            } else if (isBooleanTest(n) && !isInstrumentation(n.getParent())) {
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

    private void addBranchRecorder(Node node) {
        Node conditionNode = node.getFirstChild();
        branches.add(new Condition(conditionNode, true));
        Node wrapper = nodeHelper.wrapConditionNode(conditionNode, coverVarName, sourceFile.getName(), branches.size());
        node.replaceChild(conditionNode, wrapper);
    }

    private void addConditionRecorder(Node n) {
        log.log(Level.FINEST, "----------------------------");
        log.log(Level.FINEST, "Wrapping {0}", n);
        branches.add(new Condition(n,false));
        Node parent = n.getParent();
        Node wrapper = nodeHelper.wrapConditionNode(n, coverVarName, sourceFile.getName(), branches.size());
        log.log(Level.FINEST, "Before\n{0}", parent.toStringTree());
        parent.replaceChild(n, wrapper);
        log.log(Level.FINEST, "After\n{0}", parent.toStringTree());
    }
}
