package jscover2.instrument;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

public class NodeHelper {
    public Node wrap(Node node, String coverVarName, String urlPath, int i) {
        Node coverVar = Node.newString(Token.NAME, coverVarName);
        Node branchFunction = Node.newString(Token.STRING, "bF");
        Node elementGet = new Node(Token.GETPROP, coverVar, branchFunction);
        Node call = new Node(Token.CALL);
        call.addChildToFront(elementGet);
        call.addChildToBack(node.cloneTree());
        call.addChildToBack(Node.newString(Token.STRING, urlPath));
        call.addChildToBack(Node.newNumber(i));
        return call;
    }
}
