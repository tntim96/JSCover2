package jscover2.instrument;

import com.google.javascript.rhino.Node;

public interface AstAlteredNodeCallback {
    boolean visit(Node n);
}
