package jscover2.report;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CoverageSummaryDataSorterTest {
    private CoverageSummaryDataSorter sorter = new CoverageSummaryDataSorter();

    @Test
    public void shouldSortByName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("zzz").build());
        list.add(new CoverageSummaryDataBuilder().withName("aaa").build());
        Collections.sort(list, sorter.byName());

        assertThat(list.get(0).getName(), equalTo("aaa"));
    }
}