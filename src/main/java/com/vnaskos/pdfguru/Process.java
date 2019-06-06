package com.vnaskos.pdfguru;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Process implements ProcessListener, ProcessEventListener {

    private final ExecutionProgressListener progressListener;
    private final ProcessOrchestrator processOrchestrator;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Process(ProcessOrchestrator processOrchestrator, ExecutionProgressListener progressListener) {
        this.processOrchestrator = processOrchestrator;
        this.progressListener = progressListener;
    }

    @Override
    public void run() {
        executor.submit(() -> {
            try {
                processOrchestrator.startProcess(this);
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

    @Override
    public void started(String filePath) {
        progressListener.updateStatus(filePath);
    }

    @Override
    public void error(String message) {
        progressListener.updateStatus(message);
    }

    @Override
    public void done() {
        progressListener.incrementProgress();
    }
}
