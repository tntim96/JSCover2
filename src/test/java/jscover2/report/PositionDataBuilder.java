package jscover2.report;

import jscover2.utils.ReflectionUtils;

public class PositionDataBuilder {

    public static PositionData getPositionData(int line, int column, int length) {
        PositionData data = ReflectionUtils.newInstance(PositionData.class);
        ReflectionUtils.setField(data, "line", line);
        ReflectionUtils.setField(data, "column", column);
        ReflectionUtils.setField(data, "length", length);
        return data;
    }
}
