package com.vnaskos.pdfguru.input;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryScannerTest {

    private static Path dir;
    private static Path subDir;

    private static File supportedFile;
    private static File supportedFileInSubDir;
    private static File unsupportedFile;
    private static File unsupportedFileInSubDir;

    private final DirectoryScanner directoryScanner = new DirectoryScanner();

    @BeforeClass
    public static void createFakeFileStructure() throws IOException {
        dir = Files.createTempDirectory("pdfguru");
        subDir = Files.createTempDirectory(dir,"pdfguru_subdir");

        supportedFile = File.createTempFile("pdf","guru.pdf", dir.toFile());
        supportedFileInSubDir = File.createTempFile("pdf", "guru.pdf", subDir.toFile());
        unsupportedFile = File.createTempFile("pdf","guru.unknown", dir.toFile());
        unsupportedFileInSubDir = File.createTempFile("pdf","guru.unknown", subDir.toFile());

        supportedFile.deleteOnExit();
        supportedFileInSubDir.deleteOnExit();
        unsupportedFile.deleteOnExit();
        unsupportedFileInSubDir.deleteOnExit();
        subDir.toFile().deleteOnExit();
        dir.toFile().deleteOnExit();
    }

    @Test
    public void providingOnlyFilesShouldReturnThatFiles() {
        File[] selectedFiles = { supportedFile, unsupportedFile, supportedFileInSubDir, unsupportedFileInSubDir };

        directoryScanner.scanSubDirectoriesForSupportedFiles(selectedFiles,
                supportedFiles -> assertThat(supportedFiles)
                        .contains(supportedFile)
                        .hasSize(2));
    }

    @Test
    public void providingDirectoriesWithFilesShouldReturnOnlyTheSupportedFilesContained() {
        File[] selectedFiles = { subDir.toFile(), supportedFile, unsupportedFile };

        directoryScanner.scanSubDirectoriesForSupportedFiles(selectedFiles,
                supportedFiles -> assertThat(supportedFiles)
                        .contains(supportedFile)
                        .hasSize(2));
    }

    @Test
    public void providingDirectoriesWithDirectoriesAndFilesShouldReturnAllTheSupportedFilesContained() {
        File[] selectedFiles = { dir.toFile(), subDir.toFile(),
                supportedFile, unsupportedFile,
                supportedFileInSubDir, unsupportedFileInSubDir };

        directoryScanner.scanSubDirectoriesForSupportedFiles(selectedFiles,
                supportedFiles -> assertThat(supportedFiles)
                        .contains(supportedFile)
                        .hasSize(5));
    }

}