package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.input.DirectoryScanner;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class PDFGuruTest {

    private static final File[] THREE_INPUT_FILES = {
            new File("/path/to/file1.pdf"),
            new File("/path/to/file2.pdf"),
            new File("/path/to/file3.jpg")
    };
    private static final File[] NO_FILES = new File[0];

    @Test
    public void addFilesShouldPopulateTheListModel() {
        DirectoryScanner fakeDirectoryScanner = mock(DirectoryScanner.class);
        doReturn(Arrays.asList(THREE_INPUT_FILES)).when(fakeDirectoryScanner).getAllSupportedFiles(any());
        PDFGuru pdfGuruSpy = spy(new PDFGuru());
        doReturn(fakeDirectoryScanner).when(pdfGuruSpy).getDirectoryScanner();

        pdfGuruSpy.addElements(THREE_INPUT_FILES);

        verify(pdfGuruSpy, times(3)).addToModel(any());
    }

    @Test
    public void addFilesWithoutProvidingAnyShouldNotThrowOrPopulateModel() {
        final PDFGuru pdfGuruSpy = spy(new PDFGuru());
        pdfGuruSpy.addElements(null);
        pdfGuruSpy.addElements(NO_FILES);
        verify(pdfGuruSpy, never()).addToModel(any());
    }

}