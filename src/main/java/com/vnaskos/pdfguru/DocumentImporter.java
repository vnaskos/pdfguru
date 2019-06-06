package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.exception.ExecutionException;

public interface DocumentImporter {

    void addInputItem(InputItem inputItem, float compression) throws ExecutionException;
}
