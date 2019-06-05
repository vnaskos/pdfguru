package com.vnaskos.pdfguru.processing;

import com.vnaskos.pdfguru.ExecutionProgressListener;
import com.vnaskos.pdfguru.ProcessListener;
import com.vnaskos.pdfguru.processing.document.pdfbox.PdfboxDocumentManager;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.OutputParameters;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Process implements ProcessListener {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void run(List<InputItem> input,
                    OutputParameters outputParameters,
                    ExecutionProgressListener progressListener) {

        PdfboxDocumentManager documentManager = new PdfboxDocumentManager();
        ProcessOrchestrator orchestrator = new ProcessOrchestrator(documentManager, input, outputParameters);
        documentManager.registerProgressListener(progressListener);

        executor.submit(() -> {
            try {
                orchestrator.startProcess();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }

    @Override
    public void requestStop() {
        executor.shutdownNow();
    }
}
