package com.vnaskos.pdfguru.execution;

import com.vnaskos.pdfguru.ExecutionControlListener;
import com.vnaskos.pdfguru.execution.document.DocumentManager;
import com.vnaskos.pdfguru.input.InputItem;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class ProcessOrchestrator implements ExecutionControlListener {

    private boolean stopRequested = false;

    private final DocumentManager documentManager;
    private final List<InputItem> inputItems;
    private final OutputParameters outputParameters;

    public ProcessOrchestrator(DocumentManager documentManager, List<InputItem> inputItems,
                               OutputParameters outputParameters) {
        this.documentManager = documentManager;
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;
    }

    @Override
    public void requestStop() {
        this.stopRequested = true;
    }
    
    public void startProcess() throws IOException {
        if (outputParameters.isSingleFileOutput()) {
            startSingleOutputProcess();
        } else {
            startMultiOutputProcess();
        }
    }

    private void startMultiOutputProcess() throws IOException {
        foreachInputItem(inputItem -> {
            documentManager.openNewDocument();
            documentManager.addInputItem(inputItem, outputParameters.getCompression());
            documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
        });
    }

    private void startSingleOutputProcess() throws IOException {
        documentManager.openNewDocument();

        foreachInputItem(inputItem ->
                documentManager.addInputItem(inputItem, outputParameters.getCompression()));

        if (!stopRequested) {
            documentManager.saveDocument(outputParameters.getUniqueOutputFileName());
        }
    }

    private void foreachInputItem(InputItemProcessor process) throws IOException {
        for (InputItem inputItem : inputItems) {
            if (stopRequested) {
                break;
            }

            process.apply(inputItem);
        }
    }

    @FunctionalInterface
    private interface InputItemProcessor {
        void apply(InputItem inputItem) throws IOException;
    }
}
