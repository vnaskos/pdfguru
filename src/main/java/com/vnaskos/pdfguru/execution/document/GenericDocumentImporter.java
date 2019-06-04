package com.vnaskos.pdfguru.execution.document;

public abstract class GenericDocumentImporter<DOCUMENT, PAGE> implements DocumentImporter {

    protected final DocumentControlListener<DOCUMENT, PAGE> documentControlListener;

    public GenericDocumentImporter(DocumentControlListener<DOCUMENT, PAGE> documentControlListener) {
        this.documentControlListener = documentControlListener;
    }


}
