package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;

import java.io.File;
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

    private int fileIndex;

    public ProcessHandler(DocumentManager documentManager, List<InputItem> inputItems, OutputParameters outputParameters) {
        this.documentManager = documentManager;
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;

        fileIndex = 1;
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
        String name = getOutputName(outputParameters.getOutputFile(), fileIndex++);
        documentManager.saveDocument(name);
    }
    
    private String getOutputName(String name, int o) {
        String out;
        
        if(name.toLowerCase().endsWith(".pdf")) {
            out = name.substring(0, name.length()-4);
        } else {
            out = name;
        }
        
        String testOut = out + "_" + o + ".pdf";
        while((new File(testOut)).exists()) {
            o++;
            testOut = out + "_" + o + ".pdf";
        }
        
        return testOut;
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
