package jscover2.instrument;

import com.google.javascript.rhino.Node;

public class NodeUtil {
    static Node findFirst(Node node, NodeTest test) {
        if (test.test(node))
            return node;
        for (Node cursor = node.getFirstChild(); cursor != null; cursor = cursor.getNext())
            return findFirst(cursor, test);
        return null;
    }

}
