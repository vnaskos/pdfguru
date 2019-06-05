package com.vnaskos.pdfguru.execution.document.pdfbox;

import com.vnaskos.pdfguru.InputItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PdfboxDocumentManagerTest {

    private static final float ANY_COMPRESSION = 0.5f;

    private static final InputItem SAMPLE_5_PAGES_PDF = input("src/test/resources/5pages.pdf");
    private static final InputItem SAMPLE_128x128_IMG = input("src/test/resources/img128x128.jpg");
    private static final InputItem ENCRYPTED_5_PAGES_PDF = input("src/test/resources/5pages_encrypted.pdf");

    private final PdfboxDocumentManager managerSpy = spy(PdfboxDocumentManager.class);
    private final PDDocument fakeDoc = mock(PDDocument.class);

    @Before
    public void setup() {
        doReturn(fakeDoc).when(managerSpy).createNewDocument();

        managerSpy.openNewDocument();
    }

    @Test
    public void saveShouldCloseDocumentAndCleanupResourcesWithoutThrowingException() throws IOException {
        // intentionally added 2 PDFs to check that their documents will also close
        managerSpy.addInputItem(SAMPLE_5_PAGES_PDF, ANY_COMPRESSION);
        managerSpy.addInputItem(SAMPLE_5_PAGES_PDF, ANY_COMPRESSION);

        managerSpy.saveDocument(anyString());

        verify(managerSpy, times(1)).closeDocument();
    }

    @Test
    public void addPdfWithoutAnyPageSelectionRuleToTheNewDocumentShouldCopyAllPages() throws IOException {
        managerSpy.addInputItem(SAMPLE_5_PAGES_PDF, ANY_COMPRESSION);

        verify(managerSpy, times(5)).addPage(any());
    }

    @Test
    public void addEncryptedPdfWithoutAnyPageSelectionRuleShouldNotAddAnyPage() throws IOException {
        managerSpy.addInputItem(ENCRYPTED_5_PAGES_PDF, ANY_COMPRESSION);

        verify(managerSpy, never()).addPage(any());
    }

    @Test
    public void addImageShouldCreateAddNewPageWithSameDimensionsOnTheFinalDocument() throws IOException {
        PdfboxDocumentManager managerSpyWithRealDoc = spy(PdfboxDocumentManager.class);
        managerSpyWithRealDoc.openNewDocument();

        managerSpyWithRealDoc.addInputItem(SAMPLE_128x128_IMG, ANY_COMPRESSION);

        ArgumentCaptor<PDPage> pageCaptor = ArgumentCaptor.forClass(PDPage.class);
        verify(managerSpyWithRealDoc, times(1)).addPage(pageCaptor.capture());

        PDPage imagePage = pageCaptor.getValue();
        assertThat(imagePage.getMediaBox().getWidth()).isEqualTo(128.0f);
        assertThat(imagePage.getMediaBox().getHeight()).isEqualTo(128.0f);
    }

    @Test
    public void addImageUnableToLoadShouldContinueProcessWithoutAddingAnyPage() {
        PDDocument docSpy = spy(PDDocument.class);
        PdfboxDocumentManager managerSpyWithRealDoc = spy(PdfboxDocumentManager.class);
        doReturn(docSpy).when(managerSpyWithRealDoc).createNewDocument();
        managerSpyWithRealDoc.openNewDocument();

        InputItem nonExistingImage = new InputItem("/NON-EXISTING/IMAGE.jpg");

        managerSpyWithRealDoc.addInputItem(nonExistingImage, ANY_COMPRESSION);

        assertThat(docSpy.getNumberOfPages()).isEqualTo(0);
    }

    @Test
    public void addPdfUnableToLoadShouldContinueProcessWithoutAddingAnyPage() throws IOException {
        InputItem nonExistingPdf = new InputItem("/NON-EXISTING/PDF.pdf");

        managerSpy.addInputItem(nonExistingPdf, ANY_COMPRESSION);

        verify(managerSpy, never()).addPage(any());
    }

    private static InputItem input(String localFilePath) {
        File inputImage = new File(localFilePath);
        return new InputItem(inputImage.getAbsolutePath());
    }

}