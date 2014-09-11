package jscover2.report;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class SourceCodeRetrieverTest {
    @Test
    public void shouldRetrieveCodeSnippet() {
        SourceCodeRetriever retriever = new SourceCodeRetriever("test.js", "//Line 1\nfunction sq()\n{\n\treturn x * x;\n}");
        assertThat(retriever.getSource(PositionDataBuilder.getPositionData(1, 2, 6)), equalTo("Line 1"));
        assertThat(retriever.getSource(PositionDataBuilder.getPositionData(4, 8, 5)), equalTo("x * x"));
    }

    @Test
    public void shouldNotRetrieveCodeSnippet() {
        SourceCodeRetriever retriever = new SourceCodeRetriever("test.js", "//Line 1\nfunction sq()\n{\n\treturn x * x;\n}");
        assertThat(retriever.getSource(PositionDataBuilder.getPositionData(100, 1, 2)), nullValue());
        assertThat(retriever.getSource(PositionDataBuilder.getPositionData(1, 1, 100)), nullValue());
        assertThat(retriever.getSource(PositionDataBuilder.getPositionData(1, 100, 1)), nullValue());
    }
}
