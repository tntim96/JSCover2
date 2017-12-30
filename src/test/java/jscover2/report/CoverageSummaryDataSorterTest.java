package jscover2.report;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        list.add(new CoverageSummaryDataBuilder().withName("4").withStatementCoverage(2, 5).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withStatementCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withStatementCoverage(3, 5).build());
        list.add(new CoverageSummaryDataBuilder().withName("3").withStatementCoverage(2, 8).build());

        verifySort(list, sorter.byStatementCoverageDesc(), "2", "4", "1", "3");
        verifySort(list, sorter.byStatementCoverageAsc(), "1", "3", "4", "2");
    }

    private void verifySort(List<CoverageSummaryData> list, Comparator<CoverageSummaryData> coverageSummaryDataComparator, String s1, String s2, String s3, String s4) {
        ArrayList<CoverageSummaryData> sorted = new ArrayList<>(list);
        Collections.sort(sorted, coverageSummaryDataComparator);
        assertThat(sorted.get(0).getName(), equalTo(s1));
        assertThat(sorted.get(1).getName(), equalTo(s2));
        assertThat(sorted.get(2).getName(), equalTo(s3));
        assertThat(sorted.get(3).getName(), equalTo(s4));
    }

    @Test
    public void shouldSortByBranchCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("4").withBranchPathCoverage(2, 5).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withBranchPathCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("3").withBranchPathCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withBranchPathCoverage(3, 5).build());

        verifySort(list, sorter.byBranchCoverageDesc(), "2", "4", "1", "3");
        verifySort(list, sorter.byBranchCoverageAsc(), "1", "3", "4", "2");
    }

    @Test
    public void shouldSortByBooleanExpressionCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("4").withBooleanExpressionCoverage(2, 5).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withBooleanExpressionCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("3").withBooleanExpressionCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withBooleanExpressionCoverage(3, 5).build());

        verifySort(list, sorter.byBooleanExpressionCoverageDesc(), "2", "4", "1", "3");
        verifySort(list, sorter.byBooleanExpressionCoverageAsc(), "1", "3", "4", "2");
    }

    @Test
    public void shouldSortByFunctionCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("4").withFunctionCoverage(2, 5).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withFunctionCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("3").withFunctionCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withFunctionCoverage(3, 5).build());

        verifySort(list, sorter.byFunctionCoverageDesc(), "2", "4", "1", "3");
        verifySort(list, sorter.byFunctionCoverageAsc(), "1", "3", "4", "2");
    }

    @Test
    public void shouldSortByLineCoverageThenName() {
        List<CoverageSummaryData> list = new ArrayList<>();
        list.add(new CoverageSummaryDataBuilder().withName("4").withLineCoverage(2, 5).build());
        list.add(new CoverageSummaryDataBuilder().withName("1").withLineCoverage(1, 4).build());
        list.add(new CoverageSummaryDataBuilder().withName("3").withLineCoverage(2, 8).build());
        list.add(new CoverageSummaryDataBuilder().withName("2").withLineCoverage(3, 5).build());

        verifySort(list, sorter.byLineCoverageDesc(), "2", "4", "1", "3");
        verifySort(list, sorter.byLineCoverageAsc(), "1", "3", "4", "2");
    }
}