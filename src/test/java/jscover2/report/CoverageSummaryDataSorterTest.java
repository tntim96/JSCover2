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

        Collections.sort(list, sorter.byNameDesc());
        assertThat(list.get(0).getName(), equalTo("zzz"));

        Collections.sort(list, sorter.byNameAsc());
        assertThat(list.get(0).getName(), equalTo("aaa"));
    }

    @Test
    public void shouldSortByStatementCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("3").withStatementCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withStatementCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withStatementCoverage(3, 4).build());

        Collections.sort(list, sorter.byStatementCoverageDesc());
        assertThat(list.get(0).getName(), equalTo("2"));
        assertThat(list.get(1).getName(), equalTo("1"));
        assertThat(list.get(2).getName(), equalTo("3"));

        Collections.sort(list, sorter.byStatementCoverageAsc());
        assertThat(list.get(0).getName(), equalTo("1"));
        assertThat(list.get(1).getName(), equalTo("3"));
        assertThat(list.get(2).getName(), equalTo("2"));
    }

    @Test
    public void shouldSortByBranchCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("3").withBranchPathCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withBranchPathCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withBranchPathCoverage(3, 4).build());

        Collections.sort(list, sorter.byBranchCoverageDesc());
        assertThat(list.get(0).getName(), equalTo("2"));
        assertThat(list.get(1).getName(), equalTo("1"));
        assertThat(list.get(2).getName(), equalTo("3"));

        Collections.sort(list, sorter.byBranchCoverageAsc());
        assertThat(list.get(0).getName(), equalTo("1"));
        assertThat(list.get(1).getName(), equalTo("3"));
        assertThat(list.get(2).getName(), equalTo("2"));
    }

    @Test
    public void shouldSortByBooleanExpressionCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("3").withBooleanExpressionCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withBooleanExpressionCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withBooleanExpressionCoverage(3, 4).build());

        Collections.sort(list, sorter.byBooleanExpressionCoverageDesc());
        assertThat(list.get(0).getName(), equalTo("2"));
        assertThat(list.get(1).getName(), equalTo("1"));
        assertThat(list.get(2).getName(), equalTo("3"));

        Collections.sort(list, sorter.byBooleanExpressionCoverageAsc());
        assertThat(list.get(0).getName(), equalTo("1"));
        assertThat(list.get(1).getName(), equalTo("3"));
        assertThat(list.get(2).getName(), equalTo("2"));
    }

    @Test
    public void shouldSortByFunctionCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("3").withFunctionCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withFunctionCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withFunctionCoverage(3, 4).build());

        Collections.sort(list, sorter.byFunctionCoverageDesc());
        assertThat(list.get(0).getName(), equalTo("2"));
        assertThat(list.get(1).getName(), equalTo("1"));
        assertThat(list.get(2).getName(), equalTo("3"));

        Collections.sort(list, sorter.byFunctionCoverageAsc());
        assertThat(list.get(0).getName(), equalTo("1"));
        assertThat(list.get(1).getName(), equalTo("3"));
        assertThat(list.get(2).getName(), equalTo("2"));
    }

    @Test
    public void shouldSortByLineCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("3").withLineCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withLineCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withLineCoverage(3, 4).build());

        Collections.sort(list, sorter.byLineCoverageDesc());
        assertThat(list.get(0).getName(), equalTo("2"));
        assertThat(list.get(1).getName(), equalTo("1"));
        assertThat(list.get(2).getName(), equalTo("3"));

        Collections.sort(list, sorter.byLineCoverageAsc());
        assertThat(list.get(0).getName(), equalTo("1"));
        assertThat(list.get(1).getName(), equalTo("3"));
        assertThat(list.get(2).getName(), equalTo("2"));
    }
}