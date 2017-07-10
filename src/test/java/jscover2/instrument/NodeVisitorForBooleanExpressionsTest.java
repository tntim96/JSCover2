package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.IR;
import com.google.javascript.rhino.Node;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class NodeVisitorForBooleanExpressionsTest {
    private final SourceFile sourceFile = new SourceFile("test.js");
    private NodeVisitorForBooleanExpressions visitor = new NodeVisitorForBooleanExpressions("jscover", sourceFile, false);

    @Test
    public void shouldWrapCondition() {
        Node a = IR.name("a");
        Node b = IR.name("b");
        Node or = IR.or(a, b);
        Node expressionResult = IR.exprResult(or);
        setSourceFile(expressionResult);
        visitor.visit(a);
        visitor.visit(b);
        CodePrinter.Builder builder = new CodePrinter.Builder(expressionResult);
        assertThat(builder.build(), equalTo("jscover.beF(a,\"test.js\",1)||jscover.beF(b,\"test.js\",2)"));
    }

    @Test
    public void shouldNotWrapConditionTwice() {
        Node a = IR.name("a");
        Node b = IR.name("b");
        Node or = IR.or(a, b);
        Node expressionResult = IR.exprResult(or);
        setSourceFile(expressionResult);
        visitor.visit(a);
        visitor.visit(b);
        visitor.visit(a);
        visitor.visit(b);
        CodePrinter.Builder builder = new CodePrinter.Builder(expressionResult);
        assertThat(builder.build(), equalTo("jscover.beF(a,\"test.js\",1)||jscover.beF(b,\"test.js\",2)"));
    }

    private void setSourceFile(Node expressionResult) {
        new NodeWalker().visit(expressionResult, n -> {
            n.setSourceFileForTesting(sourceFile.getName());
        });
    }
}