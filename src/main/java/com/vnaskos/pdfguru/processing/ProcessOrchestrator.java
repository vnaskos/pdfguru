package com.vnaskos.pdfguru.processing;

import com.vnaskos.pdfguru.processing.document.DocumentManager;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.OutputParameters;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
class ProcessOrchestrator {

    private final DocumentManager documentManager;
    private final List<InputItem> inputItems;
    private final OutputParameters outputParameters;

    ProcessOrchestrator(DocumentManager documentManager, List<InputItem> inputItems,
                               OutputParameters outputParameters) {
        this.documentManager = documentManager;
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;
    }

    void startProcess() throws IOException {
        if (outputParameters.isSingleFileOutput()) {
            startSingleOutputProcess();
        } else {
            startMultiOutputProcess();
        }
    }

    private void startMultiOutputProcess() throws IOException {
        for (InputItem inputItem : inputItems) {
            documentManager.openNewDocument();
            documentManager.addInputItem(inputItem, outputParameters.getCompression());
            documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
        }
    }

    private void startSingleOutputProcess() throws IOException {
        documentManager.openNewDocument();

        for (InputItem inputItem : inputItems) {
            documentManager.addInputItem(inputItem, outputParameters.getCompression());
        }

        documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
    }
}
