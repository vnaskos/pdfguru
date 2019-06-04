package com.vnaskos.pdfguru.execution;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

public class PdfboxDocumentManager implements DocumentManager {

    private List<ExecutionProgressListener> progressListeners = new ArrayList<>();
    private PDDocument newDoc;

    private final ArrayList<PDDocument> pdfSourcesNotToBeGCd = new ArrayList<>();

    @Override
    public void openNewDocument() {
        newDoc = createNewDocument();
    }

    PDDocument createNewDocument() {
        return new PDDocument();
    }

    @Override
    public void saveDocument(String path) throws IOException {
        FileNamer fileNamer = new FileNamer();
        String filepath = fileNamer.createUniqueOutputFileName(path);
        newDoc.save(filepath);
        closeDocument();
    }

    @Override
    public void closeDocument() throws IOException {
        newDoc.close();
        for (PDDocument openSourcePdf : pdfSourcesNotToBeGCd) {
            openSourcePdf.close();
        }
        pdfSourcesNotToBeGCd.clear();
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) {
        progressListeners.forEach(l -> l.updateStatus(inputItem.getPath()));
        if (inputItem.isPdf()) {
            addPDF(inputItem);
        } else {
            addImage(inputItem, compression);
        }
        progressListeners.forEach(ExecutionProgressListener::incrementProgress);
    }

    @Override
    public void notifyFinish() {
        progressListeners.forEach(ExecutionProgressListener::finish);
    }

    public void registerProgressListener(ExecutionProgressListener listener) {
        progressListeners.add(listener);
    }

    private void addPDF(InputItem file) {
        try {
            PDDocument sourcePdf = readDocument(file.getPath());
            importSelectedPages(sourcePdf, file.getPages());
        } catch (InvalidPasswordException ex) {
            progressListeners.forEach(l -> l.updateStatus("[E02] - skip encrypted! " + file.getPath()));
        } catch (IOException ex) {
            progressListeners.forEach(l -> l.updateStatus("[E01] - can't process " + file.getPath()));
        }
    }

    private PDDocument readDocument(String filePath) throws IOException {
        PDDocument sourcePdf = PDDocument.load(new File(filePath));
        pdfSourcesNotToBeGCd.add(sourcePdf);
        return sourcePdf;
    }

    private void importSelectedPages(PDDocument sourcePdf, String selectedPagesPattern) throws IOException {
        PDDocumentCatalog cat = sourcePdf.getDocumentCatalog();
        PDPageTree pages = cat.getPages();

        for (int page : PagePatternTranslator.getSelectedIndicesFor(selectedPagesPattern, sourcePdf.getNumberOfPages())) {
            addPage(pages.get(page-1));
        }
    }

    private void addImage(InputItem file, float compression) {
        try {
            BufferedImage image = ImageIO.read(new File(file.getPath()));
            drawImageOnNewBlankPage(image, compression);
        } catch (IOException ex) {
            progressListeners.forEach(l -> l.updateStatus("[E03] - can't process image " + file.getPath()));
        }
    }

    private void drawImageOnNewBlankPage(BufferedImage image, float compression) throws IOException {
        PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
        PDImageXObject pdImage = JPEGFactory.createFromImage(newDoc, image, compression);

        try (PDPageContentStream contentStream = new PDPageContentStream(newDoc, page, APPEND, true, true)) {
            contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
        }

        addPage(page);
    }

    void addPage(PDPage page) throws IOException {
        newDoc.importPage(page);
    }
}