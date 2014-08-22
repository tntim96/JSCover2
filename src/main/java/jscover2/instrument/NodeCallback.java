package jscover2.instrument;

import com.google.javascript.rhino.Node;

public interface NodeCallback {
    void visit(Node n);
}
