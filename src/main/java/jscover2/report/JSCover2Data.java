package jscover2.report;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.HashMap;
import java.util.Map;

public class JSCover2Data {
    private Map<String, FileData> dataMap = new HashMap<>();


    public JSCover2Data(ScriptObjectMirror mirror) {
        for (String prop : mirror.keySet())
            if (!"beF".equals(prop))
                dataMap.put(prop, new FileData((ScriptObjectMirror)mirror.get(prop)));
    }

    public Map<String, FileData> getDataMap() {
        return dataMap;
    }
}
