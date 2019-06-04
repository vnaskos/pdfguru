package com.vnaskos.pdfguru.execution;

import java.io.IOException;

public interface DocumentControlListener<DOCUMENT, PAGE> {

    DOCUMENT getOutputDocument();

    void addPage(PAGE page) throws IOException;
}
