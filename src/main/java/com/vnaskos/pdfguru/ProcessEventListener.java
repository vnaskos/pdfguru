package com.vnaskos.pdfguru;

public interface ProcessEventListener {

    void started(String filePath);

    void error(String message);

    void done();
}
