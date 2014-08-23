package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeVisitorForStatements implements NodeCallback {
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> statements = new ArrayList<>();
    private String coverVarName;
    private SourceFile sourceFile;

    public NodeVisitorForStatements(String coverVarName, SourceFile sourceFile) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
    }

    public List<Node> getStatements() {
        return statements;
    }

    @Override
    public void visit(Node n) {
        if (isStatementToBeInstrumented(n))
            addStatementRecorder(n);
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

    private void addStatementRecorder(Node node) {
        statements.add(node);
        Node instrumentNode = nodeHelper.createStatementIncrementNode(coverVarName, sourceFile.getName(), statements.size());
        node.getParent().addChildBefore(instrumentNode, node);
    }
}
