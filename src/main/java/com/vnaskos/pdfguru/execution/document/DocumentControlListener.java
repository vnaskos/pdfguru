package com.vnaskos.pdfguru.execution.document;

import java.io.IOException;

public interface DocumentControlListener<DOCUMENT, PAGE> {

    DOCUMENT getOutputDocument();

    void addPage(PAGE page) throws IOException;

    void preventFromGC(DOCUMENT document);
}
