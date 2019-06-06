package com.vnaskos.pdfguru.itext;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.vnaskos.pdfguru.DocumentControlListener;
import com.vnaskos.pdfguru.DocumentManager;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.exception.ExecutionException;

import java.io.FileOutputStream;
import java.io.IOException;

public class ITextDocumentManager implements DocumentManager, DocumentControlListener<Document, PdfImportedPage> {

    private ITextPdfImporter pdfImporter;
    private ITextImageImporter imageImporter;

    private PdfCopy copy;
    private Document outputDocument;

    Document createNewDocument() {
        return new Document();
    }

    @Override
    public void openNewDocument() {
        outputDocument = createNewDocument();
    }

    @Override
    public void saveDocument(String path) throws IOException {
        try {
            openNewDocument();
            copy = new PdfCopy(outputDocument, new FileOutputStream(path));
            outputDocument.open();
            pdfImporter = new ITextPdfImporter(this, copy);
            imageImporter = new ITextImageImporter(this, copy);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDocument() {
        outputDocument.close();
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
    public Document getOutputDocument() {
        return outputDocument;
    }

    @Override
    public void addPage(PdfImportedPage page) {
        try {
            copy.addPage(page);
        } catch (IOException | BadPdfFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preventFromGC(Document document) {}
}