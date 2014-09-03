package jscover2.instrument;

import com.google.javascript.rhino.Node;

public class Decision {
    private boolean branch;//Indicates whether this is a top-level condition
    private Node node;

    public Decision(Node node, boolean branch) {
        this.branch = branch;
        this.node = node;
    }

    public boolean isBranch() {
        return branch;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return node.equals(obj);
    }
}
