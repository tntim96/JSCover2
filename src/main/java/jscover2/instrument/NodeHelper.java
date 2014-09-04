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

    private Node createIncrementNode(String coverVarName, String urlPath, String propName, int i) {
        Node getNumber = getCoverageNode(coverVarName, urlPath, propName, i);
        Node inc = new Node(Token.INC, getNumber);
        inc.putBooleanProp(Node.INCRDECR_PROP, true);
        return new Node(Token.EXPR_RESULT, inc);
    }

    private Node getCoverageNode(String coverVarName, String urlPath, String propName, int i) {
        Node coverVar = Node.newString(Token.NAME, coverVarName);
        Node path = Node.newString(Token.STRING, urlPath);
        Node getURI = new Node(Token.GETELEM, coverVar, path);
        Node prop = Node.newString(Token.STRING, propName);
        Node propGet = new Node(Token.GETPROP, getURI, prop);
        Node number = Node.newString(Token.STRING, "" + i);
        return new Node(Token.GETELEM, propGet, number);
    }

    public Node createBranchIncrementNode(String coverVarName, String urlPath, int branchNumber, int pathNumber) {
        Node getNumber = getCoverageNode(coverVarName, urlPath, "b", branchNumber);
        Node path = Node.newNumber(pathNumber);
        Node getPath = new Node(Token.GETELEM, getNumber, path);

        Node inc = new Node(Token.INC, getPath);
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
