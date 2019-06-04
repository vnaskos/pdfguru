package com.vnaskos.pdfguru.execution;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PagePatternTranslatorTest {

    private static final int LAST_PAGE = 5;

    @Test
    public void selectOnePageShouldReturnThatPageIndex() {
        final String secondPageOnly = "2";

        List<Integer> selectedPages = PagePatternTranslator.getSelectedIndicesFor(secondPageOnly, LAST_PAGE);

        assertThat(selectedPages).hasSize(1);
        assertThat(selectedPages).contains(2);
    }

    @Test
    public void selectTwoPagesShouldReturnThosePageIndices() {
        final String secondAndFourthPagesOnly = "2,4";

        List<Integer> selectedPages = PagePatternTranslator.getSelectedIndicesFor(secondAndFourthPagesOnly, LAST_PAGE);

        assertThat(selectedPages).containsExactly(2,4);
    }

    @Test
    public void selectPageRangeShouldReturnListOfAllThePageIndicesIncludedInRange() {
        final String fromTheThirdTillTheFifthPage = "3-5";

        List<Integer> selectedPages = PagePatternTranslator.getSelectedIndicesFor(fromTheThirdTillTheFifthPage, LAST_PAGE);

        assertThat(selectedPages).containsExactly(3,4,5);
    }

    @Test
    public void selectTheLastPageByProvidingTheDollarSignShouldReturnTheLastPageIndex() {
        final String onlyTheLastPage = "$";

        List<Integer> selectedPages = PagePatternTranslator.getSelectedIndicesFor(onlyTheLastPage, LAST_PAGE);

        assertThat(selectedPages).containsExactly(LAST_PAGE);
    }

    @Test
    public void selectTwoGroupsOfPagesByUsingThePipeSymbolShouldIncludeBothGroupIndices() {
        final String groupOfPages = "2,4|10-$";
        final int lastPage = 12;

        List<Integer> selectedPages = PagePatternTranslator.getSelectedIndicesFor(groupOfPages, lastPage);

        assertThat(selectedPages).containsExactly(2,4,10,11,12);
    }

    @Test
    public void selectAllPagesByProvidingEmptyPattenShouldReturnAllPageIndices() {
        final String allPages = "";
        final int lastPage = 5;

        List<Integer> selectedPages = PagePatternTranslator.getSelectedIndicesFor(allPages, lastPage);

        assertThat(selectedPages).containsExactly(1,2,3,4,5);
    }

}