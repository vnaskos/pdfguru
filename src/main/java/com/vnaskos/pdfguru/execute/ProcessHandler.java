package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private void tryToStartProcess() {
        try {
            startProcess();
        } catch (IOException ex) {
            Logger.getLogger(ProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void startProcess() throws IOException {
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

    public void execute() {
        Runnable task = this::tryToStartProcess;

        Thread t1 = new Thread(task);
        t1.start();
    }

    @Override
    public void requestStop() {
        this.stopRequested = true;
    }
}
