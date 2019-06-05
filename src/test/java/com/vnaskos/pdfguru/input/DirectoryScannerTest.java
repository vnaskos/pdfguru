package com.vnaskos.pdfguru.input;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryScannerTest {

    private static Path dir;
    private static Path subDir;

    private static File supportedFile;
    private static File unsupportedFile;

    private final DirectoryScanner directoryScanner = new DirectoryScanner();

    @BeforeClass
    public static void createFakeFileStructure() throws IOException {
        dir = Files.createTempDirectory("pdfguru");
        subDir = Files.createTempDirectory(dir,"pdfguru_subdir");

        supportedFile = File.createTempFile("pdf","guru.pdf", subDir.toFile());
        unsupportedFile = File.createTempFile("pdf","guru.unknown", dir.toFile());

        supportedFile.deleteOnExit();
        unsupportedFile.deleteOnExit();
        subDir.toFile().deleteOnExit();
        dir.toFile().deleteOnExit();
    }

    @Test
    public void providingOnlyFilesShouldReturnThatFiles() {
        File[] selectedFiles = { supportedFile };

        List<File> supportedFiles = directoryScanner.getAllSupportedFiles(selectedFiles);

        assertThat(supportedFiles)
                .contains(supportedFile)
                .hasSize(1);
    }

    @Test
    public void providingDirectoriesWithFilesShouldReturnOnlyTheSupportedFilesContained() {
        File[] selectedFiles = { dir.toFile(), dir.toFile() };

        List<File> supportedFiles = directoryScanner.getAllSupportedFiles(selectedFiles);

        assertThat(supportedFiles)
                .contains(supportedFile)
                .hasSize(2);
    }

    @Test
    public void providingDirectoriesWithDirectoriesAndFilesShouldReturnAllTheSupportedFilesContained() {
        File[] selectedFiles = { dir.toFile(), dir.toFile() };

        List<File> supportedFiles = directoryScanner.getAllSupportedFiles(selectedFiles);

        assertThat(supportedFiles)
                .contains(supportedFile)
                .hasSize(2);
    }

}