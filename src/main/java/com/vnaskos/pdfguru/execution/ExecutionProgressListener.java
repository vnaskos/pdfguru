package com.vnaskos.pdfguru.execution;

public interface ExecutionProgressListener {

    void incrementProgress();

    void updateStatus(String status);

    void finish();
}
