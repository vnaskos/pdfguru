package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.exception.ExecutionException;

import java.io.IOException;

public interface DocumentManager {

    void openNewDocument();

    void saveDocument(String path) throws IOException;

    void closeDocument() throws IOException;

    void addInputItem(InputItem inputItem, float compression) throws ExecutionException;

}
