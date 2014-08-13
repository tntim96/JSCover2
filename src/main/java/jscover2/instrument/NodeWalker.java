package jscover2.instrument;

import com.google.javascript.rhino.Node;

public class NodeWalker {
    public void visit(Node n, NodeCallback callback) {
        if (!callback.shouldTraverse(n))
            return;
        callback.visit(n);
        for (Node cursor = n.getFirstChild(); cursor != null; cursor = cursor.getNext()) {
            visit(cursor, callback);
        }
    }
}
