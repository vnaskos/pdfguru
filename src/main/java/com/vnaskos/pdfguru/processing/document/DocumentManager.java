package com.vnaskos.pdfguru.processing.document;

import com.vnaskos.pdfguru.InputItem;

import java.io.IOException;

public interface DocumentManager {

    void openNewDocument();

    void saveDocument(String path) throws IOException;

    void closeDocument() throws IOException;

    void addInputItem(InputItem inputItem, float compression);

}
