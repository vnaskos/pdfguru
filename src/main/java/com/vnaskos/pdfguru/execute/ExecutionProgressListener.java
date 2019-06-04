package com.vnaskos.pdfguru.execute;

public interface ExecutionProgressListener {

    void incrementProgress();

    void updateStatus(String status);

    void finish();
}
