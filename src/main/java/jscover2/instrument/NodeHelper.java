package jscover2.instrument;

import com.google.javascript.rhino.IR;
import com.google.javascript.rhino.Node;

public class NodeHelper {
    public Node createStatementIncrementNode(String coverVarName, String urlPath, int i) {
        return createIncrementNode(coverVarName, urlPath, "s", i);
    }

    public Node createFunctionIncrementNode(String coverVarName, String urlPath, int i) {
        return createIncrementNode(coverVarName, urlPath, "f", i);
    }

    private Node createIncrementNode(String coverVarName, String urlPath, String propName, int i) {
        Node getNumber = getCoverageNode(coverVarName, urlPath, propName, i);
        Node inc = IR.inc(getNumber, true);
        return IR.exprResult(inc);
    }

    private Node getCoverageNode(String coverVarName, String urlPath, String propName, int i) {
        Node coverVar = IR.name(coverVarName);
        Node path = IR.string(urlPath);
        Node getURI = IR.getelem(coverVar, path);
        Node prop = IR.string(propName);
        Node propGet = IR.getprop(getURI, prop);
        Node number = IR.string("" + i);
        return IR.getelem(propGet, number);
    }

    public Node createBranchIncrementNode(String coverVarName, String urlPath, int branchNumber, int pathNumber) {
        Node getNumber = getCoverageNode(coverVarName, urlPath, "b", branchNumber);
        Node path = IR.number(pathNumber);
        Node getPath = IR.getelem(getNumber, path);

        Node inc = IR.inc(getPath, true);
        return IR.exprResult(inc);
    }

    public Node wrapConditionNode(Node node, String coverVarName, String urlPath, int i) {
        Node coverVar = IR.name(coverVarName);
        Node branchFunction = IR.string("beF");
        Node elementGet = IR.getprop(coverVar, branchFunction);
        return IR.call(elementGet, node.cloneTree(), IR.string(urlPath), IR.number(i));
    }

    public boolean isInstrumentation(Node n, String coverVarName) {
        //if (n == null)
        //    return false;
        if (n.getSourceFileName() == null)
            return true;
        Node child = n.getFirstChild();
        if (child != null && child.isGetProp() && child.getFirstChild().getString().equals(coverVarName))
            return true;
        return false;
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
