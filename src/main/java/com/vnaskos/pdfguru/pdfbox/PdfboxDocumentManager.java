package com.vnaskos.pdfguru.pdfbox;

import com.vnaskos.pdfguru.DocumentControlListener;
import com.vnaskos.pdfguru.DocumentManager;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.exception.ExecutionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfboxDocumentManager implements DocumentManager, DocumentControlListener<PDDocument, PDPage> {

    private final List<PDDocument> pdfSourcesNotToBeGCd = new ArrayList<>();

    private final PdfboxImageImporter imageImporter;
    private final PdfboxPdfImporter pdfImporter;

    private PDDocument outputDocument;

    PdfboxDocumentManager() {
        imageImporter = new PdfboxImageImporter(this);
        pdfImporter = new PdfboxPdfImporter(this);
    }

    PDDocument createNewDocument() {
        return new PDDocument();
    }

    @Override
    public void openNewDocument() {
        outputDocument = createNewDocument();
    }

    @Override
    public void saveDocument(String path) throws IOException {
        outputDocument.save(path);
        closeDocument();
    }

    @Override
    public void closeDocument() throws IOException {
        outputDocument.close();
        for (PDDocument openSourcePdf : pdfSourcesNotToBeGCd) {
            openSourcePdf.close();
        }
        pdfSourcesNotToBeGCd.clear();
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) throws ExecutionException {
        if (inputItem.isPdf()) {
            pdfImporter.addInputItem(inputItem, compression);
        } else {
            imageImporter.addInputItem(inputItem, compression);
        }
    }

    @Override
    public PDDocument getOutputDocument() {
        return outputDocument;
    }

    @Override
    public void addPage(PDPage page) throws IOException {
        outputDocument.importPage(page);
    }

    @Override
    public void preventFromGC(PDDocument document) {
        pdfSourcesNotToBeGCd.add(document);
    }
}