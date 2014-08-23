package jscover2.instrument;

import com.google.javascript.rhino.Node;

public class NodeWalker {
    private boolean state = false;

    public boolean visit(Node n, NodeCallback callback) {
        callback.visit(n);
        for (Node cursor = n.getFirstChild(); cursor != null; cursor = cursor.getNext())
            state = state || visit(cursor, callback);
        return state;
    }
}
