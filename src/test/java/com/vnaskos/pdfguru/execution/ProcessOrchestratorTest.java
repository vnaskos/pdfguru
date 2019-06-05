package com.vnaskos.pdfguru.execution;

import com.vnaskos.pdfguru.execution.document.DocumentManager;
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

public class ProcessOrchestratorTest {

    private final DocumentManager fakeDocumentManager = mock(DocumentManager.class);

    private static final List<InputItem> SINGLE_FILE_INPUT = input("src/test/resources/5pages.pdf");
    private static final List<InputItem> THREE_FILES_INPUT = input(
            "src/test/resources/5pages.pdf",
            "src/test/resources/5pages.pdf",
            "src/test/resources/img128x128.jpg");

    private final OutputParameters fakeOutput = new OutputParameters("FAKE_OUTPUT_FILENAME");

    @Test
    public void saveLocalInputItemToSinglePdf() throws IOException {
        ProcessOrchestrator processOrchestrator = new ProcessOrchestrator(fakeDocumentManager, SINGLE_FILE_INPUT, fakeOutput);

        processOrchestrator.startProcess();

        InOrder inOrder = inOrder(fakeDocumentManager);
        inOrder.verify(fakeDocumentManager, atLeastOnce()).openNewDocument();
        inOrder.verify(fakeDocumentManager, times(1)).saveDocument(any());
    }

    @Test
    public void saveMultipleLocalInputItemsToIndividualPdfOneForEachInput() throws IOException {
        fakeOutput.setMultiFileOutput();
        ProcessOrchestrator processOrchestrator = new ProcessOrchestrator(fakeDocumentManager, THREE_FILES_INPUT, fakeOutput);

        processOrchestrator.startProcess();

        InOrder inOrder = inOrder(fakeDocumentManager);
        inOrder.verify(fakeDocumentManager).openNewDocument();
        inOrder.verify(fakeDocumentManager).saveDocument(any());

        verify(fakeDocumentManager, atLeast(3)).saveDocument(any());
    }

    @Test
    public void whenCancelRequestedByUserProcessShouldStop() throws IOException {
        ProcessOrchestrator processOrchestrator = new ProcessOrchestrator(fakeDocumentManager, THREE_FILES_INPUT, fakeOutput);

        processOrchestrator.requestStop();
        processOrchestrator.startProcess();

        verify(fakeDocumentManager, never()).saveDocument(any());
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
