package jscover2.instrument;

import com.google.javascript.rhino.Node;

public class NodeWrapper {
    private int id;
    private Node node;

    public NodeWrapper(int id, Node node) {
        this.id = id;
        this.node = node;
    }

    public int getId() {
        return id;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public boolean equals(Object o) {
        return node.equals(((NodeWrapper)o).getNode());
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
