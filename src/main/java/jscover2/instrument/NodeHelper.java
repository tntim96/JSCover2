package jscover2.instrument;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

public class NodeHelper {
    public Node createIncrementStatementNode(String coverVarName, String urlPath, int i) {
        Node coverVar = Node.newString(Token.NAME, coverVarName);
        Node path = Node.newString(Token.STRING, urlPath);
        Node elementGet = new Node(Token.GETELEM, coverVar, path);
        Node statementProp = Node.newString(Token.STRING, "s");
        Node propGet = new Node(Token.GETPROP, elementGet, statementProp);
        Node statementNumber = Node.newString(Token.STRING, ""+i);
        Node elementGet2 = new Node(Token.GETELEM, propGet, statementNumber);
        Node inc = new Node(Token.INC, elementGet2);
        inc.putBooleanProp(Node.INCRDECR_PROP, true);
        return new Node(Token.EXPR_RESULT, inc);
    }

    public Node wrapConditionNode(Node node, String coverVarName, String urlPath, int i) {
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
