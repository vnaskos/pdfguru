package com.vnaskos.pdfguru.pdfbox;

import com.vnaskos.pdfguru.*;
import com.vnaskos.pdfguru.exception.ExecutionException;

import java.io.IOException;
import java.util.List;

class PdfboxProcessOrchestrator implements ProcessOrchestrator {

    private final DocumentManager documentManager;
    private final List<InputItem> inputItems;
    private final OutputParameters outputParameters;
    private ProcessEventListener processEventListener;

    PdfboxProcessOrchestrator(DocumentManager documentManager,
                              List<InputItem> inputItems, OutputParameters outputParameters) {
        this.documentManager = documentManager;
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;
    }

    @Override
    public void startProcess(ProcessEventListener processEventListener) throws IOException {
        this.processEventListener = processEventListener;

        if (outputParameters.isSingleFileOutput()) {
            startSingleOutputProcess();
        } else {
            startMultiOutputProcess();
        }
    }

    private void startMultiOutputProcess() throws IOException {
        foreachInputItem((inputItem, compression) -> {
            documentManager.openNewDocument();
            documentManager.addInputItem(inputItem, outputParameters.getCompression());
            documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
        });
    }

    private void startSingleOutputProcess() throws IOException {
        documentManager.openNewDocument();

        foreachInputItem(documentManager::addInputItem);

        documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
    }

    private void foreachInputItem(InputItemProcessStrategy strategy) throws IOException {
        for (InputItem inputItem : inputItems) {
            processEventListener.started(inputItem.getFilePath());
            try {
                strategy.process(inputItem, outputParameters.getCompression());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            processEventListener.done();
        }
    }

    private interface InputItemProcessStrategy {
        void process(InputItem inputItem, float compression) throws ExecutionException, IOException;
    }
}
