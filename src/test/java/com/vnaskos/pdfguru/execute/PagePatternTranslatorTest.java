package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PagePatternTranslatorTest {

    private static final int LAST_PAGE = 5;

    private static final List<InputItem> SAMPLE_5_PAGES_PDF = input("src/test/resources/5pages.pdf");
    private static final List<InputItem> SAMPLE_128x128_IMG = input("src/test/resources/img128x128.jpg");

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

    private static List<InputItem> input(String... localFilePaths) {
        List<InputItem> inputFiles = new ArrayList<>();
        Arrays.stream(localFilePaths).forEach((filepath) -> {
            File inputImage = new File(filepath);
            InputItem imageItem = new InputItem(inputImage.getAbsolutePath());
            inputFiles.add(imageItem);
        });
        return inputFiles;
    }

}