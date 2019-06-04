package com.vnaskos.pdfguru.execution.document.pdfbox;

import com.vnaskos.pdfguru.exception.ExcecutionException;
import com.vnaskos.pdfguru.execution.document.DocumentControlListener;
import com.vnaskos.pdfguru.execution.document.DocumentManager;
import com.vnaskos.pdfguru.execution.ExecutionProgressListener;
import com.vnaskos.pdfguru.execution.util.FileNamer;
import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfboxDocumentManager implements DocumentManager, DocumentControlListener<PDDocument, PDPage> {

    private final FileNamer fileNamer = new FileNamer();

    private final List<ExecutionProgressListener> progressListeners = new ArrayList<>();
    private final List<PDDocument> pdfSourcesNotToBeGCd = new ArrayList<>();

    private final PdfboxImageImporter imageImporter;
    private final PdfboxPdfImporter pdfImporter;

    private PDDocument newDoc;

    public PdfboxDocumentManager() {
        imageImporter = new PdfboxImageImporter(this);
        pdfImporter = new PdfboxPdfImporter(this);
    }

    public void registerProgressListener(ExecutionProgressListener listener) {
        progressListeners.add(listener);
    }

    PDDocument createNewDocument() {
        return new PDDocument();
    }

    @Override
    public void openNewDocument() {
        newDoc = createNewDocument();
    }

    @Override
    public void saveDocument(String path) throws IOException {
        String filepath = fileNamer.createUniqueOutputFileName(path);
        getOutputDocument().save(filepath);
        closeDocument();
    }

    @Override
    public void closeDocument() throws IOException {
        getOutputDocument().close();
        for (PDDocument openSourcePdf : pdfSourcesNotToBeGCd) {
            openSourcePdf.close();
        }
        pdfSourcesNotToBeGCd.clear();
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) {
        progressListeners.forEach(l -> l.updateStatus(inputItem.getPath()));
        try {
            if (inputItem.isPdf()) {
                pdfImporter.addInputItem(inputItem, compression);
            } else {
                imageImporter.addInputItem(inputItem, compression);
            }
        } catch (ExcecutionException e) {
            progressListeners.forEach(l -> l.updateStatus(e.getMessage()));
        }
        progressListeners.forEach(ExecutionProgressListener::incrementProgress);
    }

    @Override
    public void notifyFinish() {
        progressListeners.forEach(ExecutionProgressListener::finish);
    }

    @Override
    public PDDocument getOutputDocument() {
        return newDoc;
    }

    @Override
    public void addPage(PDPage page) throws IOException {
        getOutputDocument().importPage(page);
    }

    @Override
    public void preventFromGC(PDDocument document) {
        pdfSourcesNotToBeGCd.add(document);
    }
}