package jscover2.instrument;

import com.google.javascript.rhino.Node;

public interface NodeCallback {
    boolean shouldTraverse(Node n);
    void visit(Node n);
}
