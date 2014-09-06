package jscover2.instrument;

import jscover2.report.CoverageData;
import jscover2.report.FileData;
import jscover2.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FileDataBuilder {
    private List<CoverageData> statements = new ArrayList<>();

    public FileDataBuilder withStatementCoverage(CoverageData data) {
        this.statements.add(data);
        return this;
    }

    public FileData build() {
        FileData fileData = ReflectionUtils.newInstance(FileData.class);
        ReflectionUtils.setField(fileData, "statements", statements);
        return fileData;
    }
}
