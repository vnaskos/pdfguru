package com.vnaskos.pdfguru.execution.document;

import com.vnaskos.pdfguru.InputItem;

import java.io.IOException;

public interface DocumentManager {

    void openNewDocument();

    void saveDocument(String path) throws IOException;

    void closeDocument() throws IOException;

    void addInputItem(InputItem inputItem, float compression);

}
