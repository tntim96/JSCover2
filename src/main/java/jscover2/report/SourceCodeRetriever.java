package jscover2.report;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceCodeRetriever {
    private static final Logger log = Logger.getLogger(SourceCodeRetriever.class.getName());

    private String code;
    private String name;
    private List<Integer> lineIndexes = new ArrayList<>();

    public SourceCodeRetriever(String name, String code) {
        this.name = name;
        this.code = code;
        lineIndexes.add(0);
        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '\n')
                lineIndexes.add(i+1);
        }
    }

    public int getNumberOfLines() {
        return lineIndexes.size();
    }

    public String getSource(PositionData pos) {
        if (pos.getLine() > lineIndexes.size()) {
            log.log(Level.WARNING, "Couldn''t find line {0} in file ''{1}''", new Object[]{pos.getLine(), name});
            return null;
        }
        Integer lineOffset = lineIndexes.get(pos.getLine() - 1);
        log.log(Level.INFO, "Line offset for line {0} is {1}", new Object[]{pos.getLine(), lineOffset});
        int startIndex = lineOffset + pos.getColumn();
        int endIndex = startIndex + pos.getLength();
        if (startIndex > code.length()) {
            log.log(Level.WARNING, "Couldn''t end index {0} in file ''{1}''", new Object[]{endIndex, name});
            return null;
        }
        if (endIndex > code.length()) {
            log.log(Level.WARNING, "Couldn''t end index {0} in file ''{1}''", new Object[]{endIndex, name});
            endIndex = code.length();
        }
        return code.substring(startIndex, endIndex);
    }
}
