package com.vnaskos.pdfguru.itext;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.vnaskos.pdfguru.DocumentControlListener;
import com.vnaskos.pdfguru.GenericDocumentImporter;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.exception.ExecutionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class ITextImageImporter extends GenericDocumentImporter<Document, PdfImportedPage> {

    private PdfCopy copy;

    ITextImageImporter(DocumentControlListener<Document, PdfImportedPage> documentControlListener, PdfCopy copy) {
        super(documentControlListener);
        this.copy = copy;
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) throws ExecutionException {
        try {
            InputStream in = new ByteArrayInputStream(convertImageToPdf(inputItem.getFilePath()).toByteArray());
            PdfReader reader = new PdfReader(in);
            copy.addPage(copy.getImportedPage(reader, 1));
            reader.close();
        } catch (IOException | DocumentException ex) {
            throw new ExecutionException("[E13] - can't process image " + inputItem.getFilePath());
        }
    }

    private ByteArrayOutputStream convertImageToPdf(String imagePath) throws IOException, DocumentException {
        Image image = Image.getInstance(imagePath);
        image.setAbsolutePosition(0, 0);

        Document document = new Document(new Rectangle(image.getWidth(), image.getHeight()));
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, fos);
        writer.open();
        document.open();
        document.add(image);
        document.close();
        writer.close();
        return fos;
    }
}
