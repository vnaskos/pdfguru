package com.vnaskos.pdfguru.itext;

import com.vnaskos.pdfguru.*;
import com.vnaskos.pdfguru.exception.ExecutionException;

import java.io.IOException;
import java.util.List;

class ITextProcessOrchestrator implements ProcessOrchestrator {

    private final DocumentManager documentManager;
    private final List<InputItem> inputItems;
    private final OutputParameters outputParameters;
    private ProcessEventListener processEventListener;

    ITextProcessOrchestrator(DocumentManager documentManager,
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
            documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
            documentManager.addInputItem(inputItem, outputParameters.getCompression());
            documentManager.closeDocument();
        });
    }

    private void startSingleOutputProcess() throws IOException {
        documentManager.saveDocument(outputParameters.getUniqueOutputFileName());

        foreachInputItem(documentManager::addInputItem);

        documentManager.closeDocument();
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
