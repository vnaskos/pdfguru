package com.vnaskos.pdfguru.execution.document;

public interface ExecutionProgressListener {

    void incrementProgress();

    void updateStatus(String status);

}
