package jscover2.instrument;

import com.google.javascript.jscomp.CodePrinter;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

import java.io.IOException;

import static com.google.javascript.jscomp.parsing.Config.JsDocParsing.TYPES_ONLY;
import static com.google.javascript.jscomp.parsing.Config.LanguageMode.ECMASCRIPT3;
import static com.google.javascript.jscomp.parsing.Config.RunMode.KEEP_GOING;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NodeHelperTest {
    private NodeHelper nodeHelper = new NodeHelper();

    @Test
    public void shouldCreateStatementIncrementNode() throws IOException {
        Node expected = parse("coverVar['urlPath'].s['7']++;");
        Node actual = nodeHelper.createStatementIncrementNode("coverVar", "urlPath", 7);

        assertThat(new CodePrinter.Builder(actual).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    @Test
    public void shouldCreateFunctionIncrementNode() throws IOException {
        Node expected = parse("coverVar['urlPath'].f['7']++;");
        Node actual = nodeHelper.createFunctionIncrementNode("coverVar", "urlPath", 7);

        assertThat(new CodePrinter.Builder(actual).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    @Test
    public void shouldCreateBranchIncrementNode() throws IOException {
        Node expected = parse("coverVar['urlPath'].b['7'][4]++;");
        Node actual = nodeHelper.createBranchIncrementNode("coverVar", "urlPath", 7, 4);

        assertThat(new CodePrinter.Builder(actual).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    @Test
    public void shouldWrapConditionNode() throws IOException {
        Node node = new Node(Token.LT);
        node.addChildToFront(Node.newString(Token.NAME, "x"));
        node.addChildToBack(Node.newNumber(0));

        Node expected = parse("coverVar.beF((x < 0), 'urlPath', 7)");
        Node actual = nodeHelper.wrapConditionNode(node, "coverVar", "urlPath", 7);

        assertThat(new CodePrinter.Builder(actual).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    @Test
    public void shouldWrapNodeWithParent() throws IOException {
        Node lt = new Node(Token.LT);
        Node ifNode = buildLessThanNodeWithParent(lt);

        Node expected = parse("if (coverVar.beF((x < 0), 'urlPath', 7))\n  x++;");

        Node wrapper = nodeHelper.wrapConditionNode(lt, "coverVar", "urlPath", 7);
        ifNode.replaceChild(lt, wrapper);

        assertThat(new CodePrinter.Builder(ifNode).build(), equalTo(new CodePrinter.Builder(expected).build()));
    }

    @Test
    public void shouldDetectInstrumentation() throws IOException {
        Node jscover = Node.newString("jscover");
        Node getProp = new Node(Token.GETPROP, jscover);
        Node call = new Node(Token.CALL, getProp);

        assertThat(nodeHelper.isInstrumentation(call, "anything"), is(true));//No source so must be synthetic
        call.setSourceFileForTesting("Hey");
        assertThat(nodeHelper.isInstrumentation(call, "jscover"), is(true));
        assertThat(nodeHelper.isInstrumentation(call, "jscovery"), is(false));
    }

    @Test
    public void shouldDetectWrappedNode() throws IOException {
        Node lt = new Node(Token.LT);
        buildLessThanNodeWithParent(lt);

        Node wrapped = nodeHelper.wrapConditionNode(lt, "coverVar", "urlPath", 7);

        Node ltWrapped = wrapped.getChildAtIndex(1);
        assertThat(lt == ltWrapped, is(false));
        assertThat(nodeHelper.isWrapped(lt, "coverVar"), is(false));
        assertThat(nodeHelper.isWrapped(ltWrapped, "coverVar"), is(true));
        assertThat(nodeHelper.isWrapped(ltWrapped, "coverVary"), is(false));
    }

    private Node buildLessThanNodeWithParent(Node lt) {
        lt.addChildToFront(Node.newString(Token.NAME, "x"));
        lt.addChildToBack(Node.newNumber(0));

        Node block = new Node(Token.BLOCK);
        Node exprResult = new Node(Token.EXPR_RESULT);
        Node inc = new Node(Token.INC);
        inc.putBooleanProp(Node.INCRDECR_PROP, true);
        Node name = Node.newString(Token.NAME, "x");
        inc.addChildrenToFront(name);
        exprResult.addChildToBack(inc);
        block.addChildrenToFront(exprResult);
        return new Node(Token.IF, lt, block);
    }

    private Node parse(String source) {
        return ParserRunner.parse(
                new SourceFile("test.js"),
                source,
                ParserRunner.createConfig(ECMASCRIPT3, TYPES_ONLY, KEEP_GOING, null),
                null).ast;
    }

}