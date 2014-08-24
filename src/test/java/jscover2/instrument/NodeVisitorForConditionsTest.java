package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class NodeVisitorForConditionsTest {
    private final SourceFile sourceFile = new SourceFile("test.js");
    private NodeVisitorForConditions visitor = new NodeVisitorForConditions("jscover", sourceFile);

    @Test
    public void shouldWrapCondition() {
        Node a = Node.newString(Token.NAME, "a");
        Node b = Node.newString(Token.NAME, "b");
        Node or = new Node(Token.OR, a, b);
        Node expressionResult = new Node(Token.EXPR_RESULT, or);
        setSourceFile(expressionResult);
        visitor.visit(or);
        CodePrinter.Builder builder = new CodePrinter.Builder(expressionResult);
        assertThat(builder.build(), equalTo("jscover.bF(a,\"test.js\",1)||jscover.bF(b,\"test.js\",2)"));
    }

    private void setSourceFile(Node expressionResult) {
        new NodeWalker().visit(expressionResult, new NodeCallback() {
            @Override
            public void visit(Node n) {
                n.setSourceFileForTesting(sourceFile.getName());
            }
        });
    }
}