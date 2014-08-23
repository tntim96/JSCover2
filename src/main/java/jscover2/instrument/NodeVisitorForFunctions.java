package jscover2.instrument;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeVisitorForFunctions implements NodeCallback {
    private NodeHelper nodeHelper = new NodeHelper();
    public List<Node> functions = new ArrayList<>();
    private String coverVarName;
    private SourceFile sourceFile;

    public NodeVisitorForFunctions(String coverVarName, SourceFile sourceFile) {
        this.coverVarName = coverVarName;
        this.sourceFile = sourceFile;
    }

    public List<Node> getFunctions() {
        return functions;
    }

    @Override
    public void visit(Node n) {
        if (n.isFunction())
            addFunctionRecorder(n);
    }

    private void addFunctionRecorder(Node node) {
        functions.add(node);
        Node instrumentNode = nodeHelper.createFunctionIncrementNode(coverVarName, sourceFile.getName(), functions.size());
        node.getLastChild().addChildToFront(instrumentNode);
    }
}
