package jscover2.instrument;

import com.google.javascript.rhino.Node;

public interface NodeTest {
    boolean test(Node node);
}
