package jscover2.instrument;

import com.google.javascript.rhino.Node;

public class BooleanExpression {
    private boolean branch;//Indicates whether this is for branch
    private Node node;

    public BooleanExpression(Node node, boolean branch) {
        this.branch = branch;
        this.node = node;
    }

    public boolean isBranch() {
        return branch;
    }

    public Node getNode() {
        return node;
    }
}
