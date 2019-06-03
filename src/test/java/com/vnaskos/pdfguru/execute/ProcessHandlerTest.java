package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessHandlerTest {

    private final DocumentManager fakeDocumentManager = mock(DocumentManager.class);

    private static final List<InputItem> SINGLE_FILE_INPUT = input("src/test/resources/5pages.pdf");
    private static final List<InputItem> THREE_FILES_INPUT = input(
            "src/test/resources/5pages.pdf",
            "src/test/resources/5pages.pdf",
            "src/test/resources/img128x128.jpg");

    private static final OutputParameters FAKE_OUTPUT = new OutputParameters("FAKE_OUTPUT_FILENAME");

    @Test
    public void saveLocalInputItemToSinglePdf() throws IOException {
        ProcessHandler processHandler = new ProcessHandler(fakeDocumentManager, SINGLE_FILE_INPUT, FAKE_OUTPUT);

        processHandler.startProcess();

        InOrder inOrder = inOrder(fakeDocumentManager);
        inOrder.verify(fakeDocumentManager, atLeastOnce()).openNewDocument();
        inOrder.verify(fakeDocumentManager, times(1)).saveDocument(any());
    }

    @Test
    public void saveMultipleLocalInputItemsToIndividualPdfOneForEachInput() throws IOException {
        OutputParameters multiFileOutput = new OutputParameters("FAKE_OUTPUT_FILEPATH");
        multiFileOutput.setMultiFileOutput();
        ProcessHandler processHandler = new ProcessHandler(fakeDocumentManager, THREE_FILES_INPUT, multiFileOutput);

        processHandler.startProcess();

        InOrder inOrder = inOrder(fakeDocumentManager);
        inOrder.verify(fakeDocumentManager).openNewDocument();
        inOrder.verify(fakeDocumentManager).saveDocument(any());

        verify(fakeDocumentManager, atLeast(3)).saveDocument(any());
    }

    @Test
    public void whenRequestedByUserStopProcess() throws IOException {
        ProcessHandler processHandler = new ProcessHandler(fakeDocumentManager, THREE_FILES_INPUT, FAKE_OUTPUT);

        processHandler.requestStop();
        processHandler.startProcess();

        verify(fakeDocumentManager, times(0)).saveDocument(any());
        verify(fakeDocumentManager, times(0)).notifyFinish();
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
