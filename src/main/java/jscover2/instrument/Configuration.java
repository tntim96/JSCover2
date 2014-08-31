package jscover2.instrument;

import com.google.javascript.jscomp.parsing.Config;

public class Configuration {
    private String coverVariableName = "jscover";
    private Config.LanguageMode javaScriptVersion = Config.LanguageMode.ECMASCRIPT6;
    private boolean includeConditions = true;

    public String getCoverVariableName() {
        return coverVariableName;
    }

    public void setCoverVariableName(String coverVariableName) {
        this.coverVariableName = coverVariableName;
    }

    public Config.LanguageMode getJavaScriptVersion() {
        return javaScriptVersion;
    }

    public boolean isIncludeConditions() {
        return includeConditions;
    }

    public void setIncludeConditions(boolean includeConditions) {
        this.includeConditions = includeConditions;
    }
}
