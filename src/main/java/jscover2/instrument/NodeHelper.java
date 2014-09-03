package jscover2.instrument;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

public class NodeHelper {
    public Node createStatementIncrementNode(String coverVarName, String urlPath, int i) {
        return createIncrementNode(coverVarName, urlPath, "s", i);
    }

    public Node createFunctionIncrementNode(String coverVarName, String urlPath, int i) {
        return createIncrementNode(coverVarName, urlPath, "f", i);
    }

    private Node createIncrementNode(String coverVarName, String urlPath, String prop, int i) {
        Node coverVar = Node.newString(Token.NAME, coverVarName);
        Node path = Node.newString(Token.STRING, urlPath);
        Node elementGet = new Node(Token.GETELEM, coverVar, path);
        Node statementProp = Node.newString(Token.STRING, prop);
        Node propGet = new Node(Token.GETPROP, elementGet, statementProp);
        Node statementNumber = Node.newString(Token.STRING, "" + i);
        Node elementGet2 = new Node(Token.GETELEM, propGet, statementNumber);
        Node inc = new Node(Token.INC, elementGet2);
        inc.putBooleanProp(Node.INCRDECR_PROP, true);
        return new Node(Token.EXPR_RESULT, inc);
    }

    public Node wrapConditionNode(Node node, String coverVarName, String urlPath, int i) {
        Node coverVar = Node.newString(Token.NAME, coverVarName);
        Node branchFunction = Node.newString(Token.STRING, "beF");
        Node elementGet = new Node(Token.GETPROP, coverVar, branchFunction);
        Node call = new Node(Token.CALL);
        call.addChildToFront(elementGet);
        call.addChildToBack(node.cloneTree());
        call.addChildToBack(Node.newString(Token.STRING, urlPath));
        call.addChildToBack(Node.newNumber(i));
        return call;
    }

    public boolean isWrapped(Node node, String coverVarName) {
        Node parent = node.getParent();
        //if (parent == null)
        //    return false;
        if (!parent.isCall())
            return false;
        Node prop = parent.getFirstChild();
        /* These should always be true
        if (prop == null)
            return false;
        if (!prop.isGetProp())
            return false;
        if (prop.getFirstChild()==null)
            return false;
        if (!prop.getFirstChild().isName())
            return false;
        */
        return prop.getFirstChild().getString().equals(coverVarName);
    }
}
