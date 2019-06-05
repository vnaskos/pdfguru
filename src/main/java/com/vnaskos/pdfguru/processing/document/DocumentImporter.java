package com.vnaskos.pdfguru.processing.document;

import com.vnaskos.pdfguru.exception.ExcecutionException;
import com.vnaskos.pdfguru.InputItem;

public interface DocumentImporter {

    void addInputItem(InputItem inputItem, float compression) throws ExcecutionException;
}
