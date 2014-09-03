package jscover2.instrument;

import com.google.javascript.jscomp.parsing.Config;

public class Configuration {
    private String coverVariableName = "jscover";
    private Config.LanguageMode javaScriptVersion = Config.LanguageMode.ECMASCRIPT6;
    private boolean includeStatements = true;
    private boolean includeFunctions = true;
    private boolean includeBranches = true;
    private boolean includeDecisions = true;
    private int maxParses = 10000000;

    public String getCoverVariableName() {
        return coverVariableName;
    }

    public void setCoverVariableName(String coverVariableName) {
        this.coverVariableName = coverVariableName;
    }

    public Config.LanguageMode getJavaScriptVersion() {
        return javaScriptVersion;
    }

    public boolean isIncludeStatements() {
        return includeStatements;
    }

    public void setIncludeStatements(boolean includeStatements) {
        this.includeStatements = includeStatements;
    }

    public boolean isIncludeFunctions() {
        return includeFunctions;
    }

    public void setIncludeFunctions(boolean includeFunctions) {
        this.includeFunctions = includeFunctions;
    }

    public boolean isIncludeBranches() {
        return includeBranches;
    }

    public void setIncludeBranches(boolean includeBranches) {
        this.includeBranches = includeBranches;
    }

    public boolean isIncludeDecisions() {
        return includeDecisions;
    }

    public void setIncludeDecisions(boolean includeDecisions) {
        this.includeDecisions = includeDecisions;
    }

    public int getMaxParses() {
        return maxParses;
    }
}
