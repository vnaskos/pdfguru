package com.vnaskos.pdfguru.itext;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.vnaskos.pdfguru.DocumentControlListener;
import com.vnaskos.pdfguru.GenericDocumentImporter;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.exception.ExecutionException;

import java.io.IOException;

class ITextPdfImporter extends GenericDocumentImporter<Document, PdfImportedPage> {

    private PdfCopy copy;

    ITextPdfImporter(DocumentControlListener<Document, PdfImportedPage> documentControlListener, PdfCopy copy) {
        super(documentControlListener);
        this.copy = copy;
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) throws ExecutionException {
        PdfReader fileToAddReader = null;

        try {
            fileToAddReader = new PdfReader(inputItem.getFilePath());
        } catch (IOException ex) {
            System.out.println("ITextHelper.addPDFToPDF(): can't read fileToInsert: " + ex);
        }

        if (fileToAddReader == null) {
            return;
        }

        try {
            for (int j = 1; j <= fileToAddReader.getNumberOfPages(); j++) {
                documentControlListener.addPage(copy.getImportedPage(fileToAddReader, j));
            }
        } catch (IOException ex) {
            throw new ExecutionException("ITextHelper.addPDFToPDF(): can't read output location: " + ex);
        }

        fileToAddReader.close();
    }
}
