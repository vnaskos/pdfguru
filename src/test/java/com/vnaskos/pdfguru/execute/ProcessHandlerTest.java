package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.OutputDialog;
import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessHandlerTest {

    private static final float RANDOM_WIDTH = 100.0f;
    private static final float RANDOM_HEIGHT = 200.0f;

    @Test
    public void saveSinglePagePdfFromALoadedImageOnComputer()
            throws IOException, COSVisitorException {
        List<InputItem> inputFiles = new ArrayList<>();

        File inputImage = new File("src/test/resources/sample128x128.jpg");
        InputItem imageItem = new InputItem(inputImage.getAbsolutePath());
        inputFiles.add(imageItem);

        OutputParameters outputParameters = new OutputParameters.Builder().separateFiles(true).build();
        ProcessHandler processHandler = new ProcessHandler(inputFiles, outputParameters);
        ProcessHandler processHandlerSpy = spy(processHandler);
        doNothing().when(processHandlerSpy).saveFile();

        OutputDialog fakeOutputDialog = mock(OutputDialog.class); // Horrible! Do not mock classes
        when(fakeOutputDialog.isVisible()).thenReturn(true);
        doReturn(fakeOutputDialog).when(processHandlerSpy).createOutputDialog();

        processHandlerSpy.startProcess();

        verify(processHandlerSpy, times(1)).addBlankPage(128, 128);
        verify(processHandlerSpy, times(1)).saveFile();
    }

    @Test
    public void addNewPageToPdfFromALoadedImageOnComputer()
            throws IOException, COSVisitorException {
        List<InputItem> inputFiles = new ArrayList<>();

        File inputImage = new File("src/test/resources/sample128x128.jpg");
        InputItem imageItem = new InputItem(inputImage.getAbsolutePath());
        inputFiles.add(imageItem);

        OutputParameters outputParameters = new OutputParameters.Builder().build();
        ProcessHandler processHandler = new ProcessHandler(inputFiles, outputParameters);
        ProcessHandler processHandlerSpy = spy(processHandler);

        processHandlerSpy.addImage(inputFiles.get(0));

        verify(processHandlerSpy, times(1)).addBlankPage(128, 128);
    }

    @Test
    public void doNotSavePdfIfImageCouldNotLoad()
            throws IOException, COSVisitorException {
        List<InputItem> inputFiles = new ArrayList<>();

        File inputImage = new File("CORRUPTED_IMAGE");
        InputItem imageItem = new InputItem(inputImage.getAbsolutePath());
        inputFiles.add(imageItem);

        OutputParameters outputParameters = new OutputParameters.Builder().separateFiles(true).build();
        ProcessHandler processHandler = new ProcessHandler(inputFiles, outputParameters);
        ProcessHandler processHandlerSpy = spy(processHandler);
        doReturn(null).when(processHandlerSpy).loadImage(any());

        processHandlerSpy.addImage(inputFiles.get(0));

        verify(processHandlerSpy, times(0)).saveFile();
    }

    @Test
    public void createNewPageWithGivenDimensions() {
        List<InputItem> inputFiles = new ArrayList<>();
        OutputParameters outputParameters = new OutputParameters.Builder().build();
        ProcessHandler processHandler = new ProcessHandler(inputFiles, outputParameters);

        PDPage newPage = processHandler.addBlankPage(RANDOM_WIDTH, RANDOM_HEIGHT);

        assertThat(newPage.getMediaBox().getWidth(), is(RANDOM_WIDTH));
        assertThat(newPage.getMediaBox().getHeight(), is(RANDOM_HEIGHT));
    }
}
