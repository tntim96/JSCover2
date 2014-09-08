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

    @Test
    public void shouldSortByDescendingStatementCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("3").withStatementCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withStatementCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withStatementCoverage(3, 4).build());
        Collections.sort(list, sorter.byStatementCoverageDesc());

        assertThat(list.get(0).getName(), equalTo("2"));
        assertThat(list.get(1).getName(), equalTo("1"));
        assertThat(list.get(2).getName(), equalTo("3"));
    }
}