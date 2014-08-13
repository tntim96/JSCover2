package jscover2.instrument;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.SimpleSourceFile;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class NodeVisitor implements NodeTraversal.Callback {
    private Config.LanguageMode mode = Config.LanguageMode.ECMASCRIPT3;
    public List<Node> statements = new ArrayList<>();
    private SourceFile sourceFile;

    public NodeVisitor(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<Node> getStatements() {
        return statements;
    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        //Can use hint here to avoid double instrumentation
        return true;
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isExprResult())
            addStatement(n, parent);
        //System.out.println("n = " + n);
    }

    private void addStatement(Node node, Node parent) {
        statements.add(node);
        Node instrumentNode = parse(format("jscover['%s'].s['"+statements.size()+"']++;", sourceFile.getName()));
        parent.addChildBefore(instrumentNode, node);
    }


    private Node parse(String source, String... warnings) {
        Node script = ParserRunner.parse(
                sourceFile,
                source,
                ParserRunner.createConfig(true, mode, false),
                null).ast;
        return script;
    }

}
