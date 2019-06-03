package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessHandlerTest {

    private static final float RANDOM_WIDTH = 100.0f;
    private static final float RANDOM_HEIGHT = 200.0f;

    private static final List<InputItem> SAMPLE_5_PAGES_PDF = input("src/test/resources/5pages.pdf");
    private static final List<InputItem> SAMPLE_128x128_IMG = input("src/test/resources/img128x128.jpg");

    private static final OutputParameters FAKE_OUTPUT = new OutputParameters("FAKE_OUTPUT_FILENAME");

    @Test
    public void savePdfFromALoadedPdfOnComputer()
            throws IOException, COSVisitorException {
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(SAMPLE_5_PAGES_PDF, FAKE_OUTPUT));
        doNothing().when(processHandlerSpy).saveFile();

        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(5)).addPage(any());
        verify(processHandlerSpy, times(1)).saveFile();
    }

    @Test
    public void saveSinglePagePdfFromALoadedImageOnComputer()
            throws IOException, COSVisitorException {
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(SAMPLE_128x128_IMG, FAKE_OUTPUT));
        doNothing().when(processHandlerSpy).saveFile();

        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(1)).addPage(any());
        verify(processHandlerSpy, times(1)).saveFile();
    }


    @Test
    public void saveOnePdfForEachInputIndividuallyShouldCreateThreePdf()
            throws IOException, COSVisitorException {
        List<InputItem> twoFiles = input("src/test/resources/5pages.pdf",
                "src/test/resources/5pages.pdf", "src/test/resources/img128x128.jpg");
        FAKE_OUTPUT.setMultiFileOutput();
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(twoFiles, FAKE_OUTPUT));
        doNothing().when(processHandlerSpy).saveFile();

        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(11)).addPage(any());
        verify(processHandlerSpy, times(3)).saveFile();
    }

    @Test
    public void stopProcessWhenRequestedByUser() throws IOException, COSVisitorException {
        ProcessHandler processHandlerSpy = spy(new ProcessHandler(SAMPLE_5_PAGES_PDF, FAKE_OUTPUT));

        processHandlerSpy.requestStop();
        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(0)).saveFile();
    }

    @Test
    public void doNotSavePdfIfInputCouldNotBeLoaded()
            throws IOException, COSVisitorException {
        InputItem corruptedImage = new InputItem("CORRUPTED_IMAGE");

        ProcessHandler processHandlerSpy = spy(new ProcessHandler(input(), FAKE_OUTPUT));

        processHandlerSpy.addImage(corruptedImage);

        verify(processHandlerSpy, times(0)).saveFile();
    }

    @Test
    public void createNewPageWithGivenDimensions() {
        ProcessHandler processHandler = new ProcessHandler(input(), FAKE_OUTPUT);

        PDPage newPage = processHandler.addBlankPage(new PDDocument(), RANDOM_WIDTH, RANDOM_HEIGHT);

        assertThat(newPage.getMediaBox().getWidth(), is(RANDOM_WIDTH));
        assertThat(newPage.getMediaBox().getHeight(), is(RANDOM_HEIGHT));
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
