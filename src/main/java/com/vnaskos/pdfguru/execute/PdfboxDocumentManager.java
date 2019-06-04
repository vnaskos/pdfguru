package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
        newDoc = new PDDocument();
    }

    @Override
    public void saveDocument(String path) throws IOException {
        newDoc.save(path);
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
    public void addInputItem(InputItem inputItem, float compression) throws IOException {
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

    private void addPDF(InputItem file)
            throws IOException {
        PDDocument sourcePdf = PDDocument.load(new File(file.getPath()));
        pdfSourcesNotToBeGCd.add(sourcePdf);

        if (sourcePdf.isEncrypted()) {
            sourcePdf.close();
            progressListeners.forEach(l -> l.updateStatus("Skip encrypted! %s" + file));
            return;
        }

        PDDocumentCatalog cat = sourcePdf.getDocumentCatalog();
        PDPageTree pages = cat.getPages();
        String pagesPattern = file.getPages();

        for (int page : PagePatternTranslator.getSelectedIndicesFor(pagesPattern, sourcePdf.getNumberOfPages())) {
            addPage(pages.get(page-1));
        }
    }

    void addImage(InputItem file, float compression) throws IOException {
        BufferedImage image = loadImage(file.getPath());

        if (image == null) {
            return;
        }

        PDPage page = addBlankPage(newDoc, image.getWidth(), image.getHeight());
        PDImageXObject pdImage = JPEGFactory.createFromImage(newDoc, image, compression);

        try (PDPageContentStream contentStream = new PDPageContentStream(newDoc, page, APPEND, true, true)) {
            contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
        }
    }

    private BufferedImage loadImage(String file) {
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(new File(file));
        } catch (IOException e) {
            progressListeners.forEach(l -> l.updateStatus("Skip, couldn't load image! %s" + file));
        }

        return bufferedImage;
    }

    private PDPage addBlankPage(PDDocument document, float width, float height) {
        PDPage page = new PDPage(
                new PDRectangle(width, height));

        document.addPage(page);

        return page;
    }

    void addPage(PDPage page) throws IOException {
        newDoc.importPage(page);
    }
}