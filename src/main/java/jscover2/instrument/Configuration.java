package jscover2.instrument;

import com.google.javascript.jscomp.parsing.Config;

public class Configuration {

    private String coverVariableName = "jscover";
    private Config.LanguageMode javaScriptVersion = Config.LanguageMode.ECMASCRIPT3;

    public String getCoverVariableName() {
        return coverVariableName;
    }

    public Config.LanguageMode getJavaScriptVersion() {
        return javaScriptVersion;
    }

    public void setCoverVariableName(String coverVariableName) {
        this.coverVariableName = coverVariableName;
    }

    public void setJavaScriptVersion(Config.LanguageMode javaScriptVersion) {
        this.javaScriptVersion = javaScriptVersion;
    }


}