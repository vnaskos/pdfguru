package com.vnaskos.pdfguru.execution.document;

import com.vnaskos.pdfguru.exception.ExcecutionException;
import com.vnaskos.pdfguru.input.items.InputItem;

public interface DocumentImporter {

    void addInputItem(InputItem inputItem, float compression) throws ExcecutionException;
}
