package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class ProcessHandler implements ExecutionControlListener {

    private boolean stopRequested = false;

    private final DocumentManager documentManager;
    private final List<InputItem> inputItems;
    private final OutputParameters outputParameters;

    public ProcessHandler(DocumentManager documentManager, List<InputItem> inputItems, OutputParameters outputParameters) {
        this.documentManager = documentManager;
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;
    }
    
    public void startProcess() throws IOException {
        documentManager.openNewDocument();

        for (InputItem inputItem : inputItems) {
            if (stopRequested) {
                return;
            }

            documentManager.addInputItem(inputItem, outputParameters.getCompression());

            if (outputParameters.isMultipleFileOutput()) {
                documentManager.saveDocument(outputParameters.getOutputFile());
                documentManager.openNewDocument();
            }
        }

        if (outputParameters.isSingleFileOutput()) {
            documentManager.saveDocument(outputParameters.getOutputFile());
        }

        documentManager.notifyFinish();
    }

    @Override
    public void requestStop() {
        this.stopRequested = true;
    }
}
