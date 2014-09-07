package jscover2.report;

import jscover2.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSCover2DataBuilder {
    private Map<String, FileData> dataMap = new HashMap<>();

    public JSCover2DataBuilder withFileData(String name, FileData data) {
        dataMap.put(name, data);
        return this;
    }

    public JSCover2Data build() {
        JSCover2Data data = ReflectionUtils.newInstance(JSCover2Data.class);
        ReflectionUtils.setField(data, "dataMap", dataMap);
        return data;
    }
}
