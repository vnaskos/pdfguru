package com.vnaskos.pdfguru.processing.document;

import java.io.IOException;

public interface DocumentControlListener<DOCUMENT, PAGE> {

    DOCUMENT getOutputDocument();

    void addPage(PAGE page) throws IOException;

    void preventFromGC(DOCUMENT document);
}
