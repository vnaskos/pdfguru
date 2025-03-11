package com.vnaskos.pdfguru.pdfbox;

import com.vnaskos.pdfguru.DocumentControlListener;
import com.vnaskos.pdfguru.GenericDocumentImporter;
import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.exception.ExecutionException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import java.io.IOException;
import java.util.List;

class PdfboxPdfImporter extends GenericDocumentImporter<PDDocument, PDPage> {

    PdfboxPdfImporter(DocumentControlListener<PDDocument, PDPage> documentControlListener) {
        super(documentControlListener);
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) throws ExecutionException {
        try {
            PDDocument sourcePdf = readDocument(inputItem.getFilePath());
            importSelectedPages(sourcePdf, inputItem.getSelectedPages(sourcePdf.getNumberOfPages()));
        } catch (InvalidPasswordException ex) {
            throw new ExecutionException("[E02] - skip encrypted! " + inputItem.getFilePath());
        } catch (IOException ex) {
            throw new ExecutionException("[E01] - can't process " + inputItem.getFilePath());
        }
    }

    private PDDocument readDocument(String filePath) throws IOException {
        PDDocument sourcePdf = Loader.loadPDF(new RandomAccessReadBufferedFile(filePath));
        documentControlListener.preventFromGC(sourcePdf);
        return sourcePdf;
    }

    private void importSelectedPages(PDDocument sourcePdf, List<Integer> selectedPages) throws IOException {
        PDDocumentCatalog cat = sourcePdf.getDocumentCatalog();
        PDPageTree pages = cat.getPages();

        for (int page : selectedPages) {
            documentControlListener.addPage(pages.get(page-1));
        }
    }
}
