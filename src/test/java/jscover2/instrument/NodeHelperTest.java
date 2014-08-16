package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import jdk.nashorn.internal.ir.Block;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class NodeHelperTest {
    private NodeHelper nodeHelper = new NodeHelper();

    @Test
    public void shouldWrapNode() throws IOException {
        Node node = new Node(Token.LT);
        node.addChildToFront(Node.newString(Token.NAME, "x"));
        node.addChildToBack(Node.newNumber(0));

        Node expected = parse("coverVar.bF((x < 0), 'urlPath', 7)");
        Node actual = nodeHelper.wrap(node, "coverVar", "urlPath", 7);

        assertThat(new CodePrinter.Builder(actual).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    @Test
    public void shouldWrapNodeWithParent() throws IOException {
        Node ifNode = new Node(Token.IF);
        Node lt = new Node(Token.LT);
        lt.addChildToFront(Node.newString(Token.NAME, "x"));
        lt.addChildToBack(Node.newNumber(0));
        ifNode.addChildrenToFront(lt);
        Node block = new Node(Token.BLOCK);
        Node exprResult = new Node(Token.EXPR_RESULT);
        Node inc = new Node(Token.INC);
        inc.putBooleanProp(Node.INCRDECR_PROP, true);
        Node name = Node.newString(Token.NAME, "x");
        inc.addChildrenToFront(name);
        exprResult.addChildToBack(inc);
        block.addChildrenToFront(exprResult);
        ifNode.addChildrenToBack(block);

        Node expected = parse("if (coverVar.bF((x < 0), 'urlPath', 7))\n  x++;");

        Node wrapper = nodeHelper.wrap(lt, "coverVar", "urlPath", 7);
        ifNode.replaceChild(lt, wrapper);

        assertThat(new CodePrinter.Builder(ifNode).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    private Node parse(String source, String... warnings) {
        Node script = ParserRunner.parse(
                new SourceFile("test.js"),
                source,
                ParserRunner.createConfig(true, Config.LanguageMode.ECMASCRIPT3, false),
                null).ast;
        return script;
    }

}