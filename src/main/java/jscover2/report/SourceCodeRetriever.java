package jscover2.report;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceCodeRetriever {
    private static final Logger log = Logger.getLogger(SourceCodeRetriever.class.getName());

    private final String[] sourceLines;
    private String name;

    public SourceCodeRetriever(String name, String code) {
        this.name = name;
        sourceLines = code.split("\n");
    }

    public String getSource(PositionData pos) {
        if (pos.getLine() > sourceLines.length) {
            log.log(Level.WARNING, "Couldn''t find line {0} in file ''{1}''", new Object[]{pos.getLine(), name});
            return null;
        }
        String line = sourceLines[pos.getLine() - 1];
        int endIndex = pos.getColumn() + pos.getLength();
        if (endIndex > line.length()) {
            log.log(Level.WARNING, "Couldn''t end index {0} in line ''{1}''", new Object[]{endIndex, line});
            return null;
        }
        return line.substring(pos.getColumn(), endIndex);
    }
}
