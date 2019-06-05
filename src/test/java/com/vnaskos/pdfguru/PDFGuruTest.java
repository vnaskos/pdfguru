package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.input.DirectoryScanner;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class PDFGuruTest {

    private static final File[] THREE_INPUT_FILES = {
            new File("/path/to/file1.pdf"),
            new File("/path/to/file2.pdf"),
            new File("/path/to/file3.jpg")
    };
    private static final File[] NO_FILES = new File[0];

    private final PDFGuru pdfGuruSpy = spy(PDFGuru.class);
    private final DirectoryScanner fakeDirectoryScanner = mock(DirectoryScanner.class);

    @Before
    public void setUp() {
        doReturn((fakeDirectoryScanner)).when(pdfGuruSpy).getDirectoryScanner(any());
    }

    @Test
    public void addFilesShouldPopulateTheListModel() {
        List<File[]> threeFilesList = new ArrayList<>();
        threeFilesList.add(THREE_INPUT_FILES);
        doReturn(threeFilesList).when(fakeDirectoryScanner).getFiles();

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