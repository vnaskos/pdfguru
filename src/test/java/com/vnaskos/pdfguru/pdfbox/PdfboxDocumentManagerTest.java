package com.vnaskos.pdfguru.pdfbox;

import com.vnaskos.pdfguru.InputItem;
import com.vnaskos.pdfguru.exception.ExecutionException;
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
        when(managerSpy.createNewDocument()).thenReturn(fakeDoc);

        managerSpy.openNewDocument();
    }

    @Test
    public void saveShouldCloseDocumentAndCleanupResourcesWithoutThrowingException()
            throws IOException, ExecutionException {
        // intentionally added 2 PDFs to check that their documents will also close
        managerSpy.addInputItem(SAMPLE_5_PAGES_PDF, ANY_COMPRESSION);
        managerSpy.addInputItem(SAMPLE_5_PAGES_PDF, ANY_COMPRESSION);

        managerSpy.saveDocument(anyString());

        verify(managerSpy, times(1)).closeDocument();
    }

    @Test
    public void addPdfWithoutAnyPageSelectionRuleToTheNewDocumentShouldCopyAllPages()
            throws IOException, ExecutionException {
        managerSpy.addInputItem(SAMPLE_5_PAGES_PDF, ANY_COMPRESSION);

        verify(managerSpy, times(5)).addPage(any());
    }

    @Test(expected = ExecutionException.class)
    public void addEncryptedPdfWithoutAnyPageSelectionRuleShouldThrow() throws ExecutionException {
        managerSpy.addInputItem(ENCRYPTED_5_PAGES_PDF, ANY_COMPRESSION);
    }

    @Test
    public void addImageShouldCreateAddNewPageWithSameDimensionsOnTheFinalDocument()
            throws IOException, ExecutionException {
        PdfboxDocumentManager managerSpyWithRealDoc = spy(PdfboxDocumentManager.class);
        managerSpyWithRealDoc.openNewDocument();

        managerSpyWithRealDoc.addInputItem(SAMPLE_128x128_IMG, ANY_COMPRESSION);

        ArgumentCaptor<PDPage> pageCaptor = ArgumentCaptor.forClass(PDPage.class);
        verify(managerSpyWithRealDoc, times(1)).addPage(pageCaptor.capture());

        PDPage imagePage = pageCaptor.getValue();
        assertThat(imagePage.getMediaBox().getWidth()).isEqualTo(128.0f);
        assertThat(imagePage.getMediaBox().getHeight()).isEqualTo(128.0f);
    }

    @Test(expected = ExecutionException.class)
    public void addImageUnableToLoadShouldThrow() throws ExecutionException {
        InputItem nonExistingImage = new InputItem("/NON-EXISTING/IMAGE.jpg");

        managerSpy.addInputItem(nonExistingImage, ANY_COMPRESSION);
    }

    @Test(expected = ExecutionException.class)
    public void addPdfUnableToLoadShouldThrow() throws ExecutionException {
        InputItem nonExistingPdf = new InputItem("/NON-EXISTING/PDF.pdf");

        managerSpy.addInputItem(nonExistingPdf, ANY_COMPRESSION);
    }

    private static InputItem input(String localFilePath) {
        File inputImage = new File(localFilePath);
        return new InputItem(inputImage.getAbsolutePath());
    }

}