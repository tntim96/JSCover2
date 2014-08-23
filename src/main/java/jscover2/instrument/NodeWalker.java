package jscover2.instrument;

import com.google.javascript.rhino.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeWalker {
    public void visit(Node n, NodeCallback callback) {
        List<Node> children = new ArrayList<>();//Get a copy of the children before AST changes
        for (Node cursor = n.getFirstChild(); cursor != null; cursor = cursor.getNext())
            children.add(cursor);
        callback.visit(n);
        for (Node child : children)
            visit(child, callback);
    }
}
