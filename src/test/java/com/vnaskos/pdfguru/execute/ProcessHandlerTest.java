package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessHandlerTest {

    private static final int LAST_PAGE = 5;

    private static final List<InputItem> WHATEVER_INPUT = input();
    private static final List<InputItem> SAMPLE_5_PAGES_PDF = input("src/test/resources/5pages.pdf");
    private static final List<InputItem> SAMPLE_128x128_IMG = input("src/test/resources/img128x128.jpg");

    private static final OutputParameters FAKE_OUTPUT = new OutputParameters("FAKE_OUTPUT_FILENAME");

    @Test
    public void savePdfFromALoadedPdfOnComputer() throws IOException {
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(SAMPLE_5_PAGES_PDF, FAKE_OUTPUT));
        doNothing().when(processHandlerSpy).saveDocument();

        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(5)).addPage(any());
        verify(processHandlerSpy, times(1)).saveDocument();
    }

    @Test
    public void saveSinglePagePdfFromALoadedImageOnComputer() throws IOException {
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(SAMPLE_128x128_IMG, FAKE_OUTPUT));
        doNothing().when(processHandlerSpy).saveDocument();

        processHandlerSpy.startProcess();

        ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
        verify(processHandlerSpy, times(1)).addPage(captor.capture());
        verify(processHandlerSpy, times(1)).saveDocument();

        PDPage actualPage = captor.getValue();
        assertThat(actualPage.getMediaBox().getWidth()).isEqualTo(128.0f);
        assertThat(actualPage.getMediaBox().getHeight()).isEqualTo(128.0f);
    }


    @Test
    public void saveOnePdfForEachInputIndividuallyShouldCreateThreePdf() throws IOException {
        List<InputItem> twoFiles = input("src/test/resources/5pages.pdf",
                "src/test/resources/5pages.pdf", "src/test/resources/img128x128.jpg");
        FAKE_OUTPUT.setMultiFileOutput();
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(twoFiles, FAKE_OUTPUT));
        doNothing().when(processHandlerSpy).saveDocument();

        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(11)).addPage(any());
        verify(processHandlerSpy, times(3)).saveDocument();
    }

    @Test
    public void whenRequestedByUserStopProcess() throws IOException {
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(SAMPLE_5_PAGES_PDF, FAKE_OUTPUT));

        processHandlerSpy.requestStop();
        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(0)).saveDocument();
    }

    @Test
    public void doNotSavePdfIfInputCouldNotBeLoaded() throws IOException {
        InputItem corruptedImage = new InputItem("CORRUPTED_IMAGE");

        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        processHandlerSpy.addImage(corruptedImage);

        verify(processHandlerSpy, times(0)).saveDocument();
    }

    @Test
    public void selectOnePageShouldReturnThatPageIndex() {
        final String secondPageOnly = "2";

        InputItem inputItem = new InputItem("");
        inputItem.setPages(secondPageOnly);
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        List<Integer> selectedPages = processHandlerSpy.getSelectedPageIndicesFor(inputItem.getPages(), LAST_PAGE);

        assertThat(selectedPages).hasSize(1);
        assertThat(selectedPages).contains(2);
    }

    @Test
    public void selectTwoPagesShouldReturnThosePageIndices() {
        final String secondAndFourthPagesOnly = "2,4";

        InputItem inputItem = new InputItem("");
        inputItem.setPages(secondAndFourthPagesOnly);
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        List<Integer> selectedPages = processHandlerSpy.getSelectedPageIndicesFor(inputItem.getPages(), LAST_PAGE);

        assertThat(selectedPages).containsExactly(2,4);
    }

    @Test
    public void selectPageRangeShouldReturnListOfAllThePageIndicesIncludedInRange() {
        final String fromTheThirdTillTheFifthPage = "3-5";

        InputItem inputItem = new InputItem("");
        inputItem.setPages(fromTheThirdTillTheFifthPage);
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        List<Integer> selectedPages = processHandlerSpy.getSelectedPageIndicesFor(inputItem.getPages(), LAST_PAGE);

        assertThat(selectedPages).containsExactly(3,4,5);
    }

    @Test
    public void selectTheLastPageByProvidingTheDollarSignShouldReturnTheLastPageIndex() {
        final String onlyTheLastPage = "$";

        InputItem inputItem = new InputItem("");
        inputItem.setPages(onlyTheLastPage);
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        List<Integer> selectedPages = processHandlerSpy.getSelectedPageIndicesFor(inputItem.getPages(), LAST_PAGE);

        assertThat(selectedPages).containsExactly(LAST_PAGE);
    }

    @Test
    public void selectTwoGroupsOfPagesByUsingThePipeSymbolShouldIncludeBothGroupIndices() {
        final String groupOfPages = "2,4|10-$";
        final int lastPage = 12;

        InputItem inputItem = new InputItem("");
        inputItem.setPages(groupOfPages);
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        List<Integer> selectedPages = processHandlerSpy.getSelectedPageIndicesFor(inputItem.getPages(), lastPage);

        assertThat(selectedPages).containsExactly(2,4,10,11,12);
    }

    @Test
    public void selectAllPagesByProvidingEmptyPattenShouldReturnAllPageIndices() {
        final String allPages = "";

        InputItem inputItem = new InputItem("");
        inputItem.setPages(allPages);
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(WHATEVER_INPUT, FAKE_OUTPUT));

        List<Integer> selectedPages = processHandlerSpy.getSelectedPageIndicesFor(inputItem.getPages(), LAST_PAGE);

        assertThat(selectedPages).containsExactly(IntStream.rangeClosed(1, LAST_PAGE).boxed().toArray(Integer[]::new));
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
