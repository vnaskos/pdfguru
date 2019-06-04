package com.vnaskos.pdfguru.execution;

import com.vnaskos.pdfguru.input.items.InputItem;

import java.io.IOException;

public interface DocumentManager extends Finishable {

    void openNewDocument();

    void saveDocument(String path) throws IOException;

    void closeDocument() throws IOException;

    void addInputItem(InputItem inputItem, float compression) throws IOException;

}
