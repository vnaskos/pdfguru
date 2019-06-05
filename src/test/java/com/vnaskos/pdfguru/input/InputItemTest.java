package com.vnaskos.pdfguru.input;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InputItemTest {

    private static final int LAST_PAGE = 5;
    private static final String A_FILE_PATH = "/DOES/NOT/MATTER";

    @Test
    public void selectOnePageShouldReturnThatPageIndex() {
        final String secondPageOnly = "2";

        List<Integer> selectedPages = getSelectedPages(secondPageOnly, LAST_PAGE);

        assertThat(selectedPages).containsExactly(2);
    }

    @Test
    public void selectTwoPagesShouldReturnThosePageIndices() {
        final String secondAndFourthPagesOnly = "2,4";

        List<Integer> selectedPages = getSelectedPages(secondAndFourthPagesOnly, LAST_PAGE);

        assertThat(selectedPages).containsExactly(2,4);
    }

    @Test
    public void selectPageRangeShouldReturnListOfAllThePageIndicesIncludedInRange() {
        final String fromTheThirdTillTheFifthPage = "3-5";

        List<Integer> selectedPages = getSelectedPages(fromTheThirdTillTheFifthPage, LAST_PAGE);

        assertThat(selectedPages).containsExactly(3,4,5);
    }

    @Test
    public void selectTheLastPageByProvidingTheDollarSignShouldReturnTheLastPageIndex() {
        final String onlyTheLastPage = "$";

        List<Integer> selectedPages = getSelectedPages(onlyTheLastPage, LAST_PAGE);

        assertThat(selectedPages).containsExactly(LAST_PAGE);
    }

    @Test
    public void selectTwoGroupsOfPagesByUsingThePipeSymbolShouldIncludeBothGroupIndices() {
        final String groupOfPages = "2,4|10-$";
        final int lastPage = 12;

        List<Integer> selectedPages = getSelectedPages(groupOfPages, lastPage);

        assertThat(selectedPages).containsExactly(2,4,10,11,12);
    }

    @Test
    public void selectAllPagesByProvidingEmptyPattenShouldReturnAllPageIndices() {
        final String allPages = "";
        final int lastPage = 5;

        List<Integer> selectedPages = getSelectedPages(allPages, lastPage);

        assertThat(selectedPages).containsExactly(1,2,3,4,5);
    }

    private List<Integer> getSelectedPages(String groupOfPages, int lastPage) {
        InputItem inputItem = new InputItem(A_FILE_PATH);
        inputItem.setPagesPattern(groupOfPages);

        return inputItem.getSelectedPages(lastPage);
    }
}