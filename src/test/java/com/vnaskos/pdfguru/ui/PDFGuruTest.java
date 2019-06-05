package com.vnaskos.pdfguru.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PDFGuruTest {

    private static final File[] THREE_INPUT_FILES = {
            new File("/path/to/file1.pdf"),
            new File("/path/to/file2.pdf"),
            new File("/path/to/file3.jpg")
    };
    private static final File[] NO_FILES = new File[0];

    private final PDFGuru pdfGuruSpy = spy(new PDFGuru());

    @Test
    public void addFilesShouldPopulateTheListModel() {
        pdfGuruSpy.addElements(THREE_INPUT_FILES);

        verify(pdfGuruSpy, times(3)).addToModel(any());
    }

    @Test
    public void addFilesWithoutProvidingAnyShouldNotThrowOrPopulateModel() {
        pdfGuruSpy.addElements(null);
        pdfGuruSpy.addElements(NO_FILES);
        verify(pdfGuruSpy, never()).addToModel(any());
    }

}