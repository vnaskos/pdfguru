package com.vnaskos.pdfguru.execution.document.pdfbox;

import com.vnaskos.pdfguru.exception.ExcecutionException;
import com.vnaskos.pdfguru.execution.document.DocumentControlListener;
import com.vnaskos.pdfguru.execution.document.GenericDocumentImporter;
import com.vnaskos.pdfguru.execution.util.PagePatternTranslator;
import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import java.io.File;
import java.io.IOException;

class PdfboxPdfImporter extends GenericDocumentImporter<PDDocument, PDPage> {

    PdfboxPdfImporter(DocumentControlListener<PDDocument, PDPage> documentControlListener) {
        super(documentControlListener);
    }

    @Override
    public void addInputItem(InputItem inputItem, float compression) throws ExcecutionException {
        try {
            PDDocument sourcePdf = readDocument(inputItem.getPath());
            importSelectedPages(sourcePdf, inputItem.getPages());
        } catch (InvalidPasswordException ex) {
            throw new ExcecutionException("[E02] - skip encrypted! " + inputItem.getPath());
        } catch (IOException ex) {
            throw new ExcecutionException("[E01] - can't process " + inputItem.getPath());
        }
    }

    private PDDocument readDocument(String filePath) throws IOException {
        PDDocument sourcePdf = PDDocument.load(new File(filePath));
        documentControlListener.preventFromGC(sourcePdf);
        return sourcePdf;
    }

    private void importSelectedPages(PDDocument sourcePdf, String selectedPagesPattern) throws IOException {
        PDDocumentCatalog cat = sourcePdf.getDocumentCatalog();
        PDPageTree pages = cat.getPages();

        for (int page : PagePatternTranslator.getSelectedIndicesFor(selectedPagesPattern, sourcePdf.getNumberOfPages())) {
            documentControlListener.addPage(pages.get(page-1));
        }
    }
}
