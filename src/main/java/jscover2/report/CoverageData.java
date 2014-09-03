package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class CoverageData {
    private final Integer zero = new Integer(0);
    private CoverageItem statements;

    public CoverageData(ScriptObjectMirror mirror) {
        ScriptObjectMirror statementObject = (ScriptObjectMirror) mirror.get("s");
        int total = statementObject.size();
        int covered = 0;
        for (Object s : statementObject.values()) {
            if (!zero.equals(s))
                covered++;
        }
        statements = new CoverageItem(total, covered);
    }

    public CoverageItem getStatements() {
        return statements;
    }
}
