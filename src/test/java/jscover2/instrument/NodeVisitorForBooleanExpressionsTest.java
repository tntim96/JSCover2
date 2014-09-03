package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class NodeVisitorForBooleanExpressionsTest {
    private final SourceFile sourceFile = new SourceFile("test.js");
    private NodeVisitorForBooleanExpressions visitor = new NodeVisitorForBooleanExpressions("jscover", sourceFile, false);

    @Test
    public void shouldWrapCondition() {
        Node a = Node.newString(Token.NAME, "a");
        Node b = Node.newString(Token.NAME, "b");
        Node or = new Node(Token.OR, a, b);
        Node expressionResult = new Node(Token.EXPR_RESULT, or);
        setSourceFile(expressionResult);
        visitor.visit(a);
        visitor.visit(b);
        CodePrinter.Builder builder = new CodePrinter.Builder(expressionResult);
        assertThat(builder.build(), equalTo("jscover.beF(a,\"test.js\",1)||jscover.beF(b,\"test.js\",2)"));
    }

    @Test
    public void shouldNotWrapConditionTwice() {
        Node a = Node.newString(Token.NAME, "a");
        Node b = Node.newString(Token.NAME, "b");
        Node or = new Node(Token.OR, a, b);
        Node expressionResult = new Node(Token.EXPR_RESULT, or);
        setSourceFile(expressionResult);
        visitor.visit(a);
        visitor.visit(b);
        or = NodeUtil.findFirst(expressionResult, new NodeTest() {
            @Override
            public boolean test(Node node) {
                return node.isOr();
            }
        });
        visitor.visit(a);
        visitor.visit(b);
        CodePrinter.Builder builder = new CodePrinter.Builder(expressionResult);
        assertThat(builder.build(), equalTo("jscover.beF(a,\"test.js\",1)||jscover.beF(b,\"test.js\",2)"));
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