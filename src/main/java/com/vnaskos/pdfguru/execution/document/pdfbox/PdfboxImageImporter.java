package com.vnaskos.pdfguru.execution.document.pdfbox;

import com.vnaskos.pdfguru.exception.ExcecutionException;
import com.vnaskos.pdfguru.execution.document.DocumentControlListener;
import com.vnaskos.pdfguru.execution.document.GenericDocumentImporter;
import com.vnaskos.pdfguru.InputItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

class PdfboxImageImporter extends GenericDocumentImporter<PDDocument, PDPage> {

    PdfboxImageImporter(DocumentControlListener<PDDocument, PDPage> documentControlListener) {
        super(documentControlListener);
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) throws ExcecutionException {
        try {
            BufferedImage image = ImageIO.read(new File(inputItem.getFilePath()));
            drawImageOnNewBlankPage(image, compression);
        } catch (IOException ex) {
            throw new ExcecutionException("[E03] - can't process image " + inputItem.getFilePath());
        }
    }

    private void drawImageOnNewBlankPage(BufferedImage image, float compression) throws IOException {
        PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
        PDImageXObject pdImage = JPEGFactory.createFromImage(
                documentControlListener.getOutputDocument(), image, compression);

        try (PDPageContentStream contentStream = new PDPageContentStream
                (documentControlListener.getOutputDocument(), page, APPEND, true, true)) {
            contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
        }

        documentControlListener.addPage(page);
    }
}
