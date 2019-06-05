package com.vnaskos.pdfguru;

public interface ExecutionProgressListener {

    void incrementProgress();

    void updateStatus(String status);

}
