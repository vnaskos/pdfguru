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
    private FileNamer fileNamer;

    public ProcessHandler(DocumentManager documentManager, List<InputItem> inputItems, OutputParameters outputParameters) {
        this.documentManager = documentManager;
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;

        this.setFileNamer(new FileNamer());
    }

    void setFileNamer(FileNamer fileNamer) {
        this.fileNamer = fileNamer;
    }
    
    public void startProcess() throws IOException {
        documentManager.openNewDocument();

        for (InputItem inputItem : inputItems) {
            if (stopRequested) {
                return;
            }

            documentManager.addInputItem(inputItem, outputParameters.getCompression());

            if (outputParameters.isMultipleFileOutput()) {
                saveDocument();
                documentManager.openNewDocument();
            }
        }

        if (outputParameters.isSingleFileOutput()) {
            saveDocument();
        }

        documentManager.notifyFinish();
    }

    private void saveDocument() throws IOException {
        String name = fileNamer.createUniqueOutputFileName(outputParameters.getOutputFile());
        documentManager.saveDocument(name);
    }

    @Override
    public void requestStop() {
        this.stopRequested = true;
    }
}
